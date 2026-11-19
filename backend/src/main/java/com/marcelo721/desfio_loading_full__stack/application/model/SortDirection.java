package com.marcelo721.desfio_loading_full__stack.application.model;


import com.marcelo721.desfio_loading_full__stack.domain.exceptions.DomainValidationException;

public enum SortDirection {
    ASC,
    DESC;

    public static SortDirection parse(String value) {
        if (value == null) {
            return DESC;
        }
        return switch (value.trim().toUpperCase()) {
            case "ASC" -> ASC;
            case "DESC" -> DESC;
            default -> throw new DomainValidationException(
                    "Direção de ordenação inválida: '" + value + "'. Valores aceitos: ASC, DESC.");
        };
    }
}
