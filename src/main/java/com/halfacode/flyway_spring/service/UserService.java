package com.halfacode.flyway_spring.service;

import com.halfacode.flyway_spring.entity.UserFlyway;
import com.halfacode.flyway_spring.repository.UserRepositoryH;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class UserService {

    private final UserRepositoryH userRepository;

    public UserService(UserRepositoryH userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserFlyway> getAllUsers() {
        return userRepository.findAll();
    }

    public UserFlyway getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public UserFlyway createUser(UserFlyway user) {
        return userRepository.save(user);
    }

    public UserFlyway updateUser(Long id, UserFlyway userDetails) {
        UserFlyway user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}