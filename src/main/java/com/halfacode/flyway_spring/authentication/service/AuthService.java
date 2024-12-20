package com.halfacode.flyway_spring.authentication.service;


import com.halfacode.flyway_spring.authentication.dto.request.UserLoginRequestDto;
import com.halfacode.flyway_spring.authentication.dto.response.TokenResponseDto;
import com.halfacode.flyway_spring.authentication.error.AuthError;
import com.halfacode.flyway_spring.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

            // Retrieve user by username, or throw if not found
            var user = userRepository.findByUsername(userLoginRequestDto.username())
                    .orElseThrow(AuthError.INVALID_USERNAME_OR_PASSWORD::exception);

            // Verify password matches stored hash
            boolean authenticated = passwordEncoder.matches(userLoginRequestDto.password(), user.getPassword());

            if (!authenticated) {
                throw AuthError.INVALID_USERNAME_OR_PASSWORD.exception();
            }

            // Generate tokens upon successful authentication
            String token = jwtService.generateToken(user, 1, JwtService.TokenType.ACCESS_TOKEN);

            String refreshToken = jwtService.generateToken(user, 30, JwtService.TokenType.REFRESH_TOKEN);

            log.info("{}, User authenticated: {}", this.getClass().getSimpleName(), userLoginRequestDto.username());

            return TokenResponseDto.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {

            log.error("{}, Error authenticating user: {}", this.getClass().getSimpleName(), userLoginRequestDto.username());

            throw AuthError.UNAUTHORIZED.exception();
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

            throw AuthError.UNAUTHORIZED.exception();

        }

    }

}
