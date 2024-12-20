package com.halfacode.flyway_spring.repository;

import com.halfacode.flyway_spring.entity.UserFlyway;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryH extends JpaRepository<UserFlyway, Long> {

}