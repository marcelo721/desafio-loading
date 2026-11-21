package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.port.in.GetServiceRequestUseCase;
import br.ufc.loading.requests.domain.model.ServiceRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class GetServiceRequestService implements GetServiceRequestUseCase {

    private final ServiceRequestFinder finder;

    public GetServiceRequestService(ServiceRequestFinder finder) {
        this.finder = finder;
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceRequest execute(UUID requestId, AuthenticatedActor actor) {
        return finder.findAccessible(requestId, actor);
    }
}
