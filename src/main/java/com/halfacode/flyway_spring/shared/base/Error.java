package com.halfacode.flyway_spring.shared.base;


import com.halfacode.flyway_spring.shared.exception.HttpResponseException;
import org.springframework.http.HttpStatus;

public interface Error {

    HttpStatus getCode();

    String getMessage();

    default HttpResponseException exception() {
        return new HttpResponseException(this);
    }

}
