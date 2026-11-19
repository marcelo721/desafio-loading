package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.policy.RequestAccessPolicy;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.ResourceNotFoundException;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;

import java.util.UUID;

public class ServiceRequestFinder {

    private final ServiceRequestRepositoryPort requestRepository;
    private final RequestAccessPolicy accessPolicy;

    public ServiceRequestFinder(ServiceRequestRepositoryPort requestRepository,
                                RequestAccessPolicy accessPolicy) {
        this.requestRepository = requestRepository;
        this.accessPolicy = accessPolicy;
    }

    public ServiceRequest findAccessible(UUID requestId, AuthenticatedActor actor) {
        ServiceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada."));
        accessPolicy.ensureCanAccess(request, actor);
        return request;
    }
}
