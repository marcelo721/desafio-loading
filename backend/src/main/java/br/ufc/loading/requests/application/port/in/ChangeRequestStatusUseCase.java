package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.domain.enums.RequestStatus;
import br.ufc.loading.requests.domain.model.ServiceRequest;

import java.util.UUID;

public interface ChangeRequestStatusUseCase {

    ServiceRequest execute(UUID requestId, RequestStatus targetStatus, AuthenticatedActor actor);
}
