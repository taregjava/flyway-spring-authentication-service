package com.halfacode.flyway_spring.authentication.error;

public class AuthException extends RuntimeException {
    private final AuthError authError;

    public AuthException(AuthError authError) {
        super(authError.getMessage());
        this.authError = authError;
    }

    public AuthError getAuthError() {
        return authError;
    }
}
