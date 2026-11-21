package br.ufc.loading.requests.application.port.out;

import br.ufc.loading.requests.application.model.PageQuery;
import br.ufc.loading.requests.application.model.PageResult;
import br.ufc.loading.requests.application.model.RequestSearchCriteria;
import br.ufc.loading.requests.application.model.RequestSortField;
import br.ufc.loading.requests.application.model.SortDirection;
import br.ufc.loading.requests.domain.model.ServiceRequest;

import java.util.Optional;
import java.util.UUID;

public interface ServiceRequestRepositoryPort {

    ServiceRequest save(ServiceRequest request);

    Optional<ServiceRequest> findById(UUID id);

    void deleteById(UUID id);

    PageResult<ServiceRequest> search(RequestSearchCriteria criteria, PageQuery pageQuery,
                                      RequestSortField sortField, SortDirection sortDirection);

    long count(RequestSearchCriteria criteria);
}
