package com.marcelo721.desfio_loading_full__stack.application.port.in;


import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.model.PageResult;
import com.marcelo721.desfio_loading_full__stack.application.model.SearchServiceRequestsQuery;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;

public interface SearchServiceRequestsUseCase {

    PageResult<ServiceRequest> execute(SearchServiceRequestsQuery query, AuthenticatedActor actor);
}
