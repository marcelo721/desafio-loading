package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.in.DeleteServiceRequestUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditedEntity;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
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
