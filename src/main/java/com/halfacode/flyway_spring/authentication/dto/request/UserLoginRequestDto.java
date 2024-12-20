package com.halfacode.flyway_spring.authentication.dto.request;


import java.io.Serializable;

public record UserLoginRequestDto(

        String username,

        String password

) implements Serializable {
}