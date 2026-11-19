package com.marcelo721.desfio_loading_full__stack.application.model;


import com.marcelo721.desfio_loading_full__stack.domain.model.User;

public record AuthenticationResult(String token, User user) {
}
