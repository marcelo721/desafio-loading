package com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.adapter;

import com.marcelo721.desfio_loading_full__stack.application.port.out.UserRepositoryPort;
import com.marcelo721.desfio_loading_full__stack.domain.model.User;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import com.marcelo721.desfio_loading_full__stack.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    public UserPersistenceAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        return PersistenceMapper.toDomain(repository.save(PersistenceMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(PersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
