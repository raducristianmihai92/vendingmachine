package net.mvpmatch.vendingmachine.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UserAuthenticationException extends AuthenticationException {

    public UserAuthenticationException(String msg) {
        super(msg);
    }
}