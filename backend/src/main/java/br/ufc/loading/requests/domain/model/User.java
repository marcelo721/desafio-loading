package br.ufc.loading.requests.domain.model;

import br.ufc.loading.requests.domain.enums.Role;
import br.ufc.loading.requests.domain.exception.DomainValidationException;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public record User(UUID id, String name, String email, String passwordHash, Role role, Instant createdAt) {

    public static final int NAME_MAX_LENGTH = 100;
    public static final int EMAIL_MAX_LENGTH = 150;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public static User register(String name, String email, String passwordHash, Role role, Instant now) {
        String validName = TextRules.requireText(name, "Nome", NAME_MAX_LENGTH);
        String validEmail = TextRules.requireText(email, "E-mail", EMAIL_MAX_LENGTH).toLowerCase();
        if (!EMAIL_PATTERN.matcher(validEmail).matches()) {
            throw new DomainValidationException("E-mail inválido.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainValidationException("Senha é obrigatória.");
        }
        if (role == null) {
            throw new DomainValidationException("Perfil é obrigatório.");
        }
        return new User(UUID.randomUUID(), validName, validEmail, passwordHash, role, now);
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
