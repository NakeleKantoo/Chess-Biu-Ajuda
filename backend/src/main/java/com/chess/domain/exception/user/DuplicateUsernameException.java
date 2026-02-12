package com.chess.domain.exception.user;

import lombok.Getter;

@Getter
public class DuplicateUsernameException extends RuntimeException {

    private final String username;

    public DuplicateUsernameException(String username) {
        super("Duplicate username: " + username);
        this.username = username;
    }
    
}
