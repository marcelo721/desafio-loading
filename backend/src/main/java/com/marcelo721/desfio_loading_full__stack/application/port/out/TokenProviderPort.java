package com.marcelo721.desfio_loading_full__stack.application.port.out;


import com.marcelo721.desfio_loading_full__stack.domain.model.User;

public interface TokenProviderPort {

    String issue(User user);
}
