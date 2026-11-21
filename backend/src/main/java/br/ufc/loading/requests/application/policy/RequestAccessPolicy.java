package br.ufc.loading.requests.application.policy;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.domain.exception.ForbiddenOperationException;
import br.ufc.loading.requests.domain.model.ServiceRequest;

public class RequestAccessPolicy {

    public void ensureCanAccess(ServiceRequest request, AuthenticatedActor actor) {
        if (actor.isAdmin() || request.isOwnedBy(actor.id())) {
            return;
        }
        throw new ForbiddenOperationException("Você não tem permissão para acessar esta solicitação.");
    }
}
