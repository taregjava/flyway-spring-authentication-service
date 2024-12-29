package com.halfacode.flyway_spring.authentication.error;

import org.springframework.http.HttpStatus;

public enum AuthError {
    INVALID_USERNAME_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid username or password"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized");

    private final HttpStatus status;
    private final String message;

    AuthError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
