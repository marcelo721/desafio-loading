package br.ufc.loading.requests.application.port.in;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.PageResult;
import br.ufc.loading.requests.application.model.SearchServiceRequestsQuery;
import br.ufc.loading.requests.domain.model.ServiceRequest;

public interface SearchServiceRequestsUseCase {

    PageResult<ServiceRequest> execute(SearchServiceRequestsQuery query, AuthenticatedActor actor);
}
