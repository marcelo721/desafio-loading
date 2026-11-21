package br.ufc.loading.requests.infrastructure.adapter.out.security;

import br.ufc.loading.requests.application.model.AuthenticatedActor;
import br.ufc.loading.requests.application.port.out.TokenProviderPort;
import br.ufc.loading.requests.domain.enums.Role;
import br.ufc.loading.requests.domain.model.User;
import br.ufc.loading.requests.infrastructure.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtTokenProvider implements TokenProviderPort {

    private static final int MIN_SECRET_BYTES = 32;
    private static final String NAME_CLAIM = "name";
    private static final String ROLE_CLAIM = "role";

    private final SecretKey signingKey;
    private final Duration expiration;
    private final Clock clock;

    public JwtTokenProvider(AppProperties properties, Clock clock) {
        byte[] secretBytes = properties.jwt().secret().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("A chave JWT deve ter pelo menos " + MIN_SECRET_BYTES + " bytes.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.expiration = Duration.ofMinutes(properties.jwt().expirationMinutes());
        this.clock = clock;
    }

    @Override
    public String issue(User user) {
        Instant now = clock.instant();
        return Jwts.builder()
                .subject(user.id().toString())
                .claim(NAME_CLAIM, user.name())
                .claim(ROLE_CLAIM, user.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }

    public Optional<AuthenticatedActor> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID userId = UUID.fromString(claims.getSubject());
            String name = claims.get(NAME_CLAIM, String.class);
            Role role = Role.valueOf(claims.get(ROLE_CLAIM, String.class));
            return Optional.of(new AuthenticatedActor(userId, name, role));
        } catch (JwtException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
