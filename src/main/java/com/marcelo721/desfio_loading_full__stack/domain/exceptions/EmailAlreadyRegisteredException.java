package com.marcelo721.desfio_loading_full__stack.domain.exceptions;

public class EmailAlreadyRegisteredException extends DomainException {

    public EmailAlreadyRegisteredException() {
        super("Já existe um usuário cadastrado com este e-mail.");
    }
}
