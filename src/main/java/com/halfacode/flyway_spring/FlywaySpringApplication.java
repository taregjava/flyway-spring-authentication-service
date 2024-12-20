package com.halfacode.flyway_spring;

import com.halfacode.flyway_spring.authentication.entity.User;
import com.halfacode.flyway_spring.authentication.repository.UserRepository;
import com.halfacode.flyway_spring.shared.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class FlywaySpringApplication  implements CommandLineRunner {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(FlywaySpringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userRepository.save(User.builder()
				.username("user")
				.password(passwordEncoder.encode("password"))
				.email("")
				.name("User")
				.avatar("")
				.role(Role.USER)
				.build());
	}
}
