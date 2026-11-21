package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.command.CreateServiceRequestCommand;
import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.port.in.CreateServiceRequestUseCase;
import br.ufc.loading.requests.application.port.out.ServiceRequestRepositoryPort;
import br.ufc.loading.requests.application.port.out.StatusHistoryRepositoryPort;
import br.ufc.loading.requests.domain.enums.AuditOperation;
import br.ufc.loading.requests.domain.enums.AuditedEntity;
import br.ufc.loading.requests.domain.model.ServiceRequest;
import br.ufc.loading.requests.domain.model.StatusHistoryEntry;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

public class CreateServiceRequestService implements CreateServiceRequestUseCase {

    private final ServiceRequestRepositoryPort requestRepository;
    private final StatusHistoryRepositoryPort historyRepository;
    private final AuditTrail auditTrail;
    private final Clock clock;

    public CreateServiceRequestService(ServiceRequestRepositoryPort requestRepository,
                                       StatusHistoryRepositoryPort historyRepository,
                                       AuditTrail auditTrail,
                                       Clock clock) {
        this.requestRepository = requestRepository;
        this.historyRepository = historyRepository;
        this.auditTrail = auditTrail;
        this.clock = clock;
    }

    @Override
    @Transactional
    public ServiceRequest execute(CreateServiceRequestCommand command, AuthenticatedActor actor) {
        Instant now = clock.instant();
        ServiceRequest request = ServiceRequest.open(
                command.title(),
                command.description(),
                command.category(),
                command.priority(),
                actor.id(),
                actor.name(),
                now);

        ServiceRequest saved = requestRepository.save(request);
        historyRepository.save(StatusHistoryEntry.initial(
                saved.getId(), saved.getStatus(), actor.id(), actor.name(), now));
        auditTrail.record(AuditedEntity.SERVICE_REQUEST, saved.getId(), AuditOperation.CREATE, actor,
                "Solicitação criada: " + saved.getTitle());
        return saved;
    }
}
