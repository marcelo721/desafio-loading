package br.ufc.loading.requests.application.port.out;

import br.ufc.loading.requests.domain.model.User;

public interface TokenProviderPort {

    String issue(User user);
}
