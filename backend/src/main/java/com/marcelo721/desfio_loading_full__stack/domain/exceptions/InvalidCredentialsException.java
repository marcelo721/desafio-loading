package com.marcelo721.desfio_loading_full__stack.domain.exceptions;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("E-mail ou senha inválidos.");
    }
}
