package com.halfacode.flyway_spring.authentication.dto.response;

import java.io.Serializable;

public record GoogleUserInfoDto(

        String id,

        String email,

        String verified_email,

        String name,

        String given_name,

        String family_name,

        String picture

) implements Serializable {
}
