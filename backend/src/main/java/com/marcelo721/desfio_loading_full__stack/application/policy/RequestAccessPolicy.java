package com.marcelo721.desfio_loading_full__stack.application.policy;


import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.domain.exceptions.ForbiddenOperationException;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;

public class RequestAccessPolicy {

    public void ensureCanAccess(ServiceRequest request, AuthenticatedActor actor) {
        if (actor.isAdmin() || request.isOwnedBy(actor.id())) {
            return;
        }
        throw new ForbiddenOperationException("Você não tem permissão para acessar esta solicitação.");
    }
}
