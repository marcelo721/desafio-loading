package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.port.in.GetServiceRequestUseCase;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
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
