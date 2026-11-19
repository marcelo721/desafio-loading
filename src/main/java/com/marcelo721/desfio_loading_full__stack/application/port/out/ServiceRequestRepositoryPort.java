package com.marcelo721.desfio_loading_full__stack.application.port.out;



import com.marcelo721.desfio_loading_full__stack.application.model.*;
import com.marcelo721.desfio_loading_full__stack.domain.model.ServiceRequest;

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
