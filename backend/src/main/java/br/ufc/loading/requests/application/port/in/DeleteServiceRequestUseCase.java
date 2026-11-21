package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.model.AuthenticatedActor;

import java.util.UUID;

public interface DeleteServiceRequestUseCase {

    void execute(UUID requestId, AuthenticatedActor actor);
}
