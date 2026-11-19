package com.marcelo721.desfio_loading_full__stack.application.model;


import com.marcelo721.desfio_loading_full__stack.domain.enums.Priority;
import com.marcelo721.desfio_loading_full__stack.domain.enums.RequestStatus;

public record SearchServiceRequestsQuery(
        RequestStatus status,
        Priority priority,
        String text,
        PageQuery pageQuery,
        RequestSortField sortField,
        SortDirection sortDirection) {
}
