package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.port.in.DeleteServiceRequestUseCase;
import br.ufc.loading.requests.application.port.out.ServiceRequestRepositoryPort;
import br.ufc.loading.requests.domain.enums.AuditOperation;
import br.ufc.loading.requests.domain.enums.AuditedEntity;
import br.ufc.loading.requests.domain.model.ServiceRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class DeleteServiceRequestService implements DeleteServiceRequestUseCase {

    private final ServiceRequestFinder finder;
    private final ServiceRequestRepositoryPort requestRepository;
    private final AuditTrail auditTrail;

    public DeleteServiceRequestService(ServiceRequestFinder finder,
                                       ServiceRequestRepositoryPort requestRepository,
                                       AuditTrail auditTrail) {
        this.finder = finder;
        this.requestRepository = requestRepository;
        this.auditTrail = auditTrail;
    }

    @Override
    @Transactional
    public void execute(UUID requestId, AuthenticatedActor actor) {
        ServiceRequest request = finder.findAccessible(requestId, actor);
        requestRepository.deleteById(request.getId());
        auditTrail.record(AuditedEntity.SERVICE_REQUEST, request.getId(), AuditOperation.DELETE, actor,
                "Solicitação removida: " + request.getTitle());
    }
}
