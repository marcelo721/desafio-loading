package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.adapter;

import com.marcelo721.desfio_loading_full__stack.application.model.RequestSearchCriteria;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.entity.ServiceRequestJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

final class ServiceRequestSpecifications {

    private static final char LIKE_ESCAPE = '\\';

    private ServiceRequestSpecifications() {
    }

    static Specification<ServiceRequestJpaEntity> matching(RequestSearchCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.status() != null) {
                predicates.add(builder.equal(root.get("status"), criteria.status()));
            }
            if (criteria.priority() != null) {
                predicates.add(builder.equal(root.get("priority"), criteria.priority()));
            }
            if (criteria.ownerId() != null) {
                predicates.add(builder.equal(root.get("ownerId"), criteria.ownerId()));
            }
            if (criteria.text() != null && !criteria.text().isBlank()) {
                String pattern = "%" + escapeLike(criteria.text().trim().toLowerCase()) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.<String>get("title")), pattern, LIKE_ESCAPE),
                        builder.like(builder.lower(root.<String>get("description")), pattern, LIKE_ESCAPE),
                        builder.like(builder.lower(root.<String>get("category")), pattern, LIKE_ESCAPE)));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
