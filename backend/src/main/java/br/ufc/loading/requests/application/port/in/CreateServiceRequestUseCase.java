package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.command.CreateServiceRequestCommand;
import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.domain.model.ServiceRequest;

public interface CreateServiceRequestUseCase {

    ServiceRequest execute(CreateServiceRequestCommand command, AuthenticatedActor actor);
}
