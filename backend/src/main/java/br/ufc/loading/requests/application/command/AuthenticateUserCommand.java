package br.ufc.loading.requests.application.command;

public record AuthenticateUserCommand(String email, String password) {
}
