package com.halfacode.flyway_spring.controller;

import com.halfacode.flyway_spring.entity.UserFlyway;
import com.halfacode.flyway_spring.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserFlyway> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserFlyway> getUserById(@PathVariable Long id) {
        UserFlyway user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserFlyway> createUser(@RequestBody UserFlyway user) {
        UserFlyway newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserFlyway> updateUser(@PathVariable Long id, @RequestBody UserFlyway userDetails) {
        UserFlyway updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public String me(Authentication authentication) {

        return authentication.getName() + authentication.getAuthorities().toString();

    }
}
