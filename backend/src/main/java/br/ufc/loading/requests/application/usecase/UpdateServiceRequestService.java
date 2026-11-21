package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.command.UpdateServiceRequestCommand;
import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.port.in.UpdateServiceRequestUseCase;
import br.ufc.loading.requests.application.port.out.ServiceRequestRepositoryPort;
import br.ufc.loading.requests.domain.enums.AuditOperation;
import br.ufc.loading.requests.domain.enums.AuditedEntity;
import br.ufc.loading.requests.domain.model.ServiceRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

public class UpdateServiceRequestService implements UpdateServiceRequestUseCase {

    private final ServiceRequestFinder finder;
    private final ServiceRequestRepositoryPort requestRepository;
    private final AuditTrail auditTrail;
    private final Clock clock;

    public UpdateServiceRequestService(ServiceRequestFinder finder,
                                       ServiceRequestRepositoryPort requestRepository,
                                       AuditTrail auditTrail,
                                       Clock clock) {
        this.finder = finder;
        this.requestRepository = requestRepository;
        this.auditTrail = auditTrail;
        this.clock = clock;
    }

    @Override
    @Transactional
    public ServiceRequest execute(UUID requestId, UpdateServiceRequestCommand command, AuthenticatedActor actor) {
        ServiceRequest request = finder.findAccessible(requestId, actor);
        request.edit(command.title(), command.description(), command.category(), command.priority(),
                clock.instant());

        ServiceRequest saved = requestRepository.save(request);
        auditTrail.record(AuditedEntity.SERVICE_REQUEST, saved.getId(), AuditOperation.UPDATE, actor,
                "Solicitação atualizada: " + saved.getTitle());
        return saved;
    }
}
