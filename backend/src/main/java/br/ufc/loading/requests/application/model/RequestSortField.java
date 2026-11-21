package br.ufc.loading.requests.application.model;

import br.ufc.loading.requests.domain.exception.DomainValidationException;

import java.util.Arrays;

public enum RequestSortField {
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    TITLE("title"),
    PRIORITY("priority"),
    STATUS("status");

    private final String property;

    RequestSortField(String property) {
        this.property = property;
    }

    public String property() {
        return property;
    }

    public static RequestSortField fromProperty(String value) {
        return Arrays.stream(values())
                .filter(field -> field.property.equals(value))
                .findFirst()
                .orElseThrow(() -> new DomainValidationException(
                        "Campo de ordenação inválido: '" + value
                                + "'. Valores aceitos: createdAt, updatedAt, title, priority, status."));
    }
}
