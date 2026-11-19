package com.marcelo721.desfio_loading_full__stack.application.port.in;



import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;

import java.util.UUID;

public interface GetServiceRequestUseCase {

    ServiceRequest execute(UUID requestId, AuthenticatedActor actor);
}
