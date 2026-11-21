package br.ufc.loading.requests.domain.exception;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("E-mail ou senha inválidos.");
    }
}
