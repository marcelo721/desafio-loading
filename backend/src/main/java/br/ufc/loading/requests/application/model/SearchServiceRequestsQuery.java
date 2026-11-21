package br.ufc.loading.requests.application.model;

import br.ufc.loading.requests.domain.enums.Priority;
import br.ufc.loading.requests.domain.enums.RequestStatus;

public record SearchServiceRequestsQuery(
        RequestStatus status,
        Priority priority,
        String text,
        PageQuery pageQuery,
        RequestSortField sortField,
        SortDirection sortDirection) {
}
