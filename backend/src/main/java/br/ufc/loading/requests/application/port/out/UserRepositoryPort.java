package br.ufc.loading.requests.application.port.out;

import br.ufc.loading.requests.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
