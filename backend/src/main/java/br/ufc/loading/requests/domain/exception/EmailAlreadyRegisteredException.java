package br.ufc.loading.requests.domain.exception;

public class EmailAlreadyRegisteredException extends DomainException {

    public EmailAlreadyRegisteredException() {
        super("Já existe um usuário cadastrado com este e-mail.");
    }
}
