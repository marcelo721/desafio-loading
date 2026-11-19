package br.ufc.loading.requests.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.command.CreateServiceRequestCommand;
import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.out.AuditLogPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.StatusHistoryRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.application.usecase.AuditTrail;
import com.marcelo721.desfio_loading_full__stack.application.usecase.CreateServiceRequestService;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.Priority;
import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;
import com.marcelo721.desfio_loading_full__stack.domain.enums.Role;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.DomainValidationException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateServiceRequestServiceTest {

    private static final Instant NOW = Instant.parse("2026-03-10T12:00:00Z");

    @Mock
    private ServiceRequestRepositoryPort requestRepository;

    @Mock
    private StatusHistoryRepositoryPort historyRepository;

    @Mock
    private AuditLogPort auditLogPort;

    private CreateServiceRequestService service;

    private final AuthenticatedActor actor = new AuthenticatedActor(UUID.randomUUID(), "Maria", Role.USER);

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new CreateServiceRequestService(requestRepository, historyRepository,
                new AuditTrail(auditLogPort, clock), clock);
    }

    @Test
    void shouldCreateOpenRequestOwnedByActorWithInitialHistoryAndAudit() {
        when(requestRepository.save(any(ServiceRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceRequest created = service.execute(
                new CreateServiceRequestCommand("Broken chair", "Chair in room 4", "Furniture", Priority.LOW), actor);

        assertThat(created.getStatus()).isEqualTo(RequestStatus.OPEN);
        assertThat(created.getOwnerId()).isEqualTo(actor.id());
        assertThat(created.getOwnerName()).isEqualTo("Maria");
        assertThat(created.getCreatedAt()).isEqualTo(NOW);

        ArgumentCaptor<StatusHistoryEntry> historyCaptor = ArgumentCaptor.forClass(StatusHistoryEntry.class);
        verify(historyRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().fromStatus()).isNull();
        assertThat(historyCaptor.getValue().toStatus()).isEqualTo(RequestStatus.OPEN);

        ArgumentCaptor<AuditEntry> auditCaptor = ArgumentCaptor.forClass(AuditEntry.class);
        verify(auditLogPort).save(auditCaptor.capture());
        assertThat(auditCaptor.getValue().operation()).isEqualTo(AuditOperation.CREATE);
        assertThat(auditCaptor.getValue().entityId()).isEqualTo(created.getId());
    }

    @Test
    void shouldRejectInvalidDataBeforeTouchingAnyPort() {
        assertThatThrownBy(() -> service.execute(
                new CreateServiceRequestCommand(" ", "Chair in room 4", "Furniture", Priority.LOW), actor))
                .isInstanceOf(DomainValidationException.class);

        verify(requestRepository, never()).save(any());
        verifyNoInteractions(historyRepository, auditLogPort);
    }
}
