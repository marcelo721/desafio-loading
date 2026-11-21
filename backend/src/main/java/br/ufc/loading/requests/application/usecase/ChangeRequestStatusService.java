package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.port.in.ChangeRequestStatusUseCase;
import br.ufc.loading.requests.application.port.out.ServiceRequestRepositoryPort;
import br.ufc.loading.requests.application.port.out.StatusHistoryRepositoryPort;
import br.ufc.loading.requests.domain.enums.AuditOperation;
import br.ufc.loading.requests.domain.enums.AuditedEntity;
import br.ufc.loading.requests.domain.enums.RequestStatus;
import br.ufc.loading.requests.domain.model.ServiceRequest;
import br.ufc.loading.requests.domain.model.StatusHistoryEntry;
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
