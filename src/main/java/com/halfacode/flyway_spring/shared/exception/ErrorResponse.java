package com.halfacode.flyway_spring.shared.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public record ErrorResponse(

        HttpStatus status,

        String message

) implements Serializable {
}
