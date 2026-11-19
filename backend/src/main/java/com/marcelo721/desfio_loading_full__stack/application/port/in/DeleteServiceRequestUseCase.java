package com.marcelo721.desfio_loading_full__stack.application.port.in;


import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;

import java.util.UUID;

public interface DeleteServiceRequestUseCase {

    void execute(UUID requestId, AuthenticatedActor actor);
}
