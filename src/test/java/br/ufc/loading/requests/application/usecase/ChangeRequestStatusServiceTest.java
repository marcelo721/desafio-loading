package br.ufc.loading.requests.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.policy.RequestAccessPolicy;
import com.marcelo721.desfio_loading_full__stack.application.port.out.AuditLogPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.StatusHistoryRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.application.usecase.AuditTrail;
import com.marcelo721.desfio_loading_full__stack.application.usecase.ChangeRequestStatusService;
import com.marcelo721.desfio_loading_full__stack.application.usecase.ServiceRequestFinder;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.Priority;
import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;
import com.marcelo721.desfio_loading_full__stack.domain.enums.Role;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.ForbiddenOperationException;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.InvalidStatusTransitionException;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.ResourceNotFoundException;
import com.marcelo721.desfio_loading_full__stack.domain.model.AuditEntry;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
import com.marcelo721.desfio_loading_full__stack.domain.model.StatusHistoryEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeRequestStatusServiceTest {

    private static final Instant NOW = Instant.parse("2026-03-10T12:00:00Z");

    @Mock
    private ServiceRequestRepositoryPort requestRepository;

    @Mock
    private StatusHistoryRepositoryPort historyRepository;

    @Mock
    private AuditLogPort auditLogPort;

    private ChangeRequestStatusService service;

    private final AuthenticatedActor owner = new AuthenticatedActor(UUID.randomUUID(), "Owner", Role.USER);

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        ServiceRequestFinder finder = new ServiceRequestFinder(requestRepository, new RequestAccessPolicy());
        service = new ChangeRequestStatusService(finder, requestRepository, historyRepository,
                new AuditTrail(auditLogPort, clock), clock);
    }

    private ServiceRequest openRequestOwnedBy(AuthenticatedActor actor) {
        return ServiceRequest.open("Title", "Description", "IT", Priority.HIGH,
                actor.id(), actor.name(), NOW.minusSeconds(60));
    }

    @Test
    void shouldAdvanceStatusAndRecordHistoryAndAudit() {
        ServiceRequest request = openRequestOwnedBy(owner);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(requestRepository.save(any(ServiceRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceRequest result = service.execute(request.getId(), RequestStatus.IN_PROGRESS, owner);

        assertThat(result.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        assertThat(result.getUpdatedAt()).isEqualTo(NOW);

        ArgumentCaptor<StatusHistoryEntry> historyCaptor = ArgumentCaptor.forClass(StatusHistoryEntry.class);
        verify(historyRepository).save(historyCaptor.capture());
        StatusHistoryEntry history = historyCaptor.getValue();
        assertThat(history.requestId()).isEqualTo(request.getId());
        assertThat(history.fromStatus()).isEqualTo(RequestStatus.OPEN);
        assertThat(history.toStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        assertThat(history.changedById()).isEqualTo(owner.id());
        assertThat(history.changedAt()).isEqualTo(NOW);

        ArgumentCaptor<AuditEntry> auditCaptor = ArgumentCaptor.forClass(AuditEntry.class);
        verify(auditLogPort).save(auditCaptor.capture());
        assertThat(auditCaptor.getValue().operation()).isEqualTo(AuditOperation.STATUS_CHANGE);
        assertThat(auditCaptor.getValue().userId()).isEqualTo(owner.id());
    }

    @Test
    void shouldRejectSkippingStepsWithoutPersistingAnything() {
        ServiceRequest request = openRequestOwnedBy(owner);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.execute(request.getId(), RequestStatus.COMPLETED, owner))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(requestRepository, never()).save(any());
        verifyNoInteractions(historyRepository, auditLogPort);
    }

    @Test
    void shouldRejectGoingBackwards() {
        ServiceRequest request = openRequestOwnedBy(owner);
        request.moveTo(RequestStatus.IN_PROGRESS, NOW);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.execute(request.getId(), RequestStatus.OPEN, owner))
                .isInstanceOf(InvalidStatusTransitionException.class);

        verify(requestRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenRequestDoesNotExist() {
        UUID missingId = UUID.randomUUID();
        when(requestRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(missingId, RequestStatus.IN_PROGRESS, owner))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldForbidUsersThatDoNotOwnTheRequest() {
        ServiceRequest request = openRequestOwnedBy(owner);
        AuthenticatedActor stranger = new AuthenticatedActor(UUID.randomUUID(), "Stranger", Role.USER);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.execute(request.getId(), RequestStatus.IN_PROGRESS, stranger))
                .isInstanceOf(ForbiddenOperationException.class);

        verify(requestRepository, never()).save(any());
    }

    @Test
    void shouldAllowAdministratorsToChangeAnyRequest() {
        ServiceRequest request = openRequestOwnedBy(owner);
        AuthenticatedActor admin = new AuthenticatedActor(UUID.randomUUID(), "Admin", Role.ADMIN);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(requestRepository.save(any(ServiceRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceRequest result = service.execute(request.getId(), RequestStatus.IN_PROGRESS, admin);

        assertThat(result.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
    }
}
