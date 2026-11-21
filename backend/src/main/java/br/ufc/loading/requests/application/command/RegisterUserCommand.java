package br.ufc.loading.requests.application.command;

public record RegisterUserCommand(String name, String email, String password) {
}
