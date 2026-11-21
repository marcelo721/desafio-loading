package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.command.UpdateServiceRequestCommand;
import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.domain.model.ServiceRequest;

import java.util.UUID;

public interface UpdateServiceRequestUseCase {

    ServiceRequest execute(UUID requestId, UpdateServiceRequestCommand command, AuthenticatedActor actor);
}
