package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.in.ChangeRequestStatusUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.StatusHistoryRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditedEntity;
import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
import com.marcelo721.desfio_loading_full__stack.domain.model.StatusHistoryEntry;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class ChangeRequestStatusService implements ChangeRequestStatusUseCase {

    private final ServiceRequestFinder finder;
    private final ServiceRequestRepositoryPort requestRepository;
    private final StatusHistoryRepositoryPort historyRepository;
    private final AuditTrail auditTrail;
    private final Clock clock;

    public ChangeRequestStatusService(ServiceRequestFinder finder,
                                      ServiceRequestRepositoryPort requestRepository,
                                      StatusHistoryRepositoryPort historyRepository,
                                      AuditTrail auditTrail,
                                      Clock clock) {
        this.finder = finder;
        this.requestRepository = requestRepository;
        this.historyRepository = historyRepository;
        this.auditTrail = auditTrail;
        this.clock = clock;
    }

    @Override
    @Transactional
    public ServiceRequest execute(UUID requestId, RequestStatus targetStatus, AuthenticatedActor actor) {
        ServiceRequest request = finder.findAccessible(requestId, actor);
        Instant now = clock.instant();

        RequestStatus previousStatus = request.moveTo(targetStatus, now);

        ServiceRequest saved = requestRepository.save(request);
        historyRepository.save(StatusHistoryEntry.transition(
                saved.getId(), previousStatus, saved.getStatus(), actor.id(), actor.name(), now));
        auditTrail.record(AuditedEntity.SERVICE_REQUEST, saved.getId(), AuditOperation.STATUS_CHANGE, actor,
                "Status alterado de '" + previousStatus.displayName() + "' para '"
                        + saved.getStatus().displayName() + "': " + saved.getTitle());
        return saved;
    }
}
