package com.halfacode.flyway_spring.authentication.service;


import com.halfacode.flyway_spring.authentication.dto.request.UserLoginRequestDto;
import com.halfacode.flyway_spring.authentication.dto.response.TokenResponseDto;
import com.halfacode.flyway_spring.authentication.entity.User;
import com.halfacode.flyway_spring.authentication.error.AuthError;
import com.halfacode.flyway_spring.authentication.error.AuthException;
import com.halfacode.flyway_spring.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    public TokenResponseDto authenticated(UserLoginRequestDto userLoginRequestDto) {
        try {
            log.info("{}, Authenticating user: {}", this.getClass().getSimpleName(), userLoginRequestDto.username());

            var user = userRepository.findByUsername(userLoginRequestDto.username())
                    .orElseThrow(() -> {
                        log.error("User not found for username: {}", userLoginRequestDto.username());
                        return new AuthException(AuthError.INVALID_USERNAME_OR_PASSWORD);
                    });

            boolean authenticated = passwordEncoder.matches(userLoginRequestDto.password(), user.getPassword());
            if (!authenticated) {
                log.error("Password mismatch for username: {}", userLoginRequestDto.username());
                throw new AuthException(AuthError.INVALID_USERNAME_OR_PASSWORD);
            }

            String token = jwtService.generateToken(user, 1, JwtService.TokenType.ACCESS_TOKEN);
            String refreshToken = jwtService.generateToken(user, 30, JwtService.TokenType.REFRESH_TOKEN);

            log.info("{}, User authenticated: {}", this.getClass().getSimpleName(), userLoginRequestDto.username());

            return TokenResponseDto.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();

        } catch (AuthException e) {
            log.error("{}, Authentication error: {}", this.getClass().getSimpleName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("{}, Error authenticating user: {}", this.getClass().getSimpleName(), userLoginRequestDto.username(), e);
            throw new AuthException(AuthError.UNAUTHORIZED);
        }
    }




    public TokenResponseDto refreshToken(String refreshToken) {

        try {

            log.info("{}, Refreshing token: {}", this.getClass().getSimpleName(), refreshToken);

            // Refresh token generation through JwtService
            TokenResponseDto tokenResponseDto = jwtService.refreshToken(refreshToken);

            log.info("{}, Token refreshed: {}", this.getClass().getSimpleName(), refreshToken);

            return tokenResponseDto;

        } catch (Exception e) {

            log.error("{}, Error refreshing token: {}", this.getClass().getSimpleName(), refreshToken);
            throw new AuthException(AuthError.UNAUTHORIZED);

        }

    }

}
