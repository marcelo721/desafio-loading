package br.ufc.loading.requests.application.model;

import br.ufc.loading.requests.domain.model.User;

public record AuthenticationResult(String token, User user) {
}
