package com.marcelo721.desfio_loading_full__stack.application.port.in;


import com.marcelo721.desfio_loading_full__stack.application.command.CreateServiceRequestCommand;
import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;

public interface CreateServiceRequestUseCase {

    ServiceRequest execute(CreateServiceRequestCommand command, AuthenticatedActor actor);
}
