package com.halfacode.flyway_spring.authentication.dto.response;

import java.io.Serializable;

public record GoogleTokenResponseDto(

        String access_token,

        String expires_in,

        String scope,

        String token_type,

        String id_token

) implements Serializable {
}
