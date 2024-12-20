package com.halfacode.flyway_spring.shared.exception;
import com.halfacode.flyway_spring.shared.base.Error;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HttpResponseException extends RuntimeException {

    private final HttpStatus statusCode;

    public HttpResponseException(Error error) {

        super(error.getMessage());

        this.statusCode = error.getCode();

    }

}
