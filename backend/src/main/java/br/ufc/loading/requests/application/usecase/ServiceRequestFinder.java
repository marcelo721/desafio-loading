package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.policy.RequestAccessPolicy;
import br.ufc.loading.requests.application.port.out.ServiceRequestRepositoryPort;
import br.ufc.loading.requests.domain.exception.ResourceNotFoundException;
import br.ufc.loading.requests.domain.model.ServiceRequest;

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
