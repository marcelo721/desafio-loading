package br.ufc.loading.requests.application.usecase;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.model.PageResult;
import br.ufc.loading.requests.application.model.RequestSearchCriteria;
import br.ufc.loading.requests.application.model.SearchServiceRequestsQuery;
import br.ufc.loading.requests.application.port.in.SearchServiceRequestsUseCase;
import br.ufc.loading.requests.application.port.out.ServiceRequestRepositoryPort;
import br.ufc.loading.requests.domain.model.ServiceRequest;
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
