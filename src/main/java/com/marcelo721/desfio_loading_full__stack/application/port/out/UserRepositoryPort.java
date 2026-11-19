package com.marcelo721.desfio_loading_full__stack.application.port.out;


import com.marcelo721.desfio_loading_full__stack.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
