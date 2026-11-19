package com.marcelo721.desfio_loading_full__stack.application.model;


import com.marcelo721.desfio_loading_full__stack.domain.exceptions.DomainValidationException;

public record PageQuery(int page, int size) {

    public static final int MAX_SIZE = 100;

    public PageQuery {
        if (page < 0) {
            throw new DomainValidationException("A página deve ser maior ou igual a zero.");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new DomainValidationException("O tamanho da página deve estar entre 1 e " + MAX_SIZE + ".");
        }
    }
}
