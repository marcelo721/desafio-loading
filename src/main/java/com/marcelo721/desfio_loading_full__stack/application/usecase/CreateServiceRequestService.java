package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.command.CreateServiceRequestCommand;
import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.in.CreateServiceRequestUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.application.port.out.StatusHistoryRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditOperation;
import com.marcelo721.desfio_loading_full__stack.domain.enums.AuditedEntity;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
import com.marcelo721.desfio_loading_full__stack.domain.model.StatusHistoryEntry;
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
