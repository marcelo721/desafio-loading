package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.command.UpdateServiceRequestCommand;
import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.in.UpdateServiceRequestUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditedEntity;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
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
