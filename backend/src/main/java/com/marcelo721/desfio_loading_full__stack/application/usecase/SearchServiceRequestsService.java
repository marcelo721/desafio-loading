package com.marcelo721.desfio_loading_full__stack.application.usecase;

import com.marcelo721.desfio_loading_full__stack.application.model.AuthenticatedActor;
import com.marcelo721.desfio_loading_full__stack.application.model.PageResult;
import com.marcelo721.desfio_loading_full__stack.application.model.RequestSearchCriteria;
import com.marcelo721.desfio_loading_full__stack.application.model.SearchServiceRequestsQuery;
import com.marcelo721.desfio_loading_full__stack.application.port.in.SearchServiceRequestsUseCase;
import com.marcelo721.desfio_loading_full__stack.application.port.out.ServiceRequestRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class SearchServiceRequestsService implements SearchServiceRequestsUseCase {

    private final ServiceRequestRepositoryPort requestRepository;

    public SearchServiceRequestsService(ServiceRequestRepositoryPort requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ServiceRequest> execute(SearchServiceRequestsQuery query, AuthenticatedActor actor) {
        UUID ownerScope = actor.isAdmin() ? null : actor.id();
        RequestSearchCriteria criteria = new RequestSearchCriteria(
                query.status(), query.priority(), query.text(), ownerScope);
        return requestRepository.search(criteria, query.pageQuery(), query.sortField(), query.sortDirection());
    }
}
