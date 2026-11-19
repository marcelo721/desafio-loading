package com.marcelo721.desfio_loading_full__stack.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Cors cors, BootstrapAdmin bootstrapAdmin) {

    public record Jwt(String secret, long expirationMinutes) {
    }

    public record Cors(List<String> allowedOrigins) {
    }

    public record BootstrapAdmin(String name, String email, String password) {
    }
}
