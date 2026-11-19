package com.marcelo721.desfio_loading_full__stack.application.port.in;



import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;

import java.util.UUID;

public interface ChangeRequestStatusUseCase {

    ServiceRequest execute(UUID requestId, RequestStatus targetStatus, AuthenticatedActor actor);
}
