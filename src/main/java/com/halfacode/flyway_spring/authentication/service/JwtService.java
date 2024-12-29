package com.halfacode.flyway_spring.authentication.service;

import com.halfacode.flyway_spring.authentication.dto.response.TokenResponseDto;
import com.halfacode.flyway_spring.authentication.entity.User;
import com.halfacode.flyway_spring.authentication.error.AuthError;
import com.halfacode.flyway_spring.authentication.error.AuthException;
import com.halfacode.flyway_spring.authentication.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.signer-key}")
    private String signerKey;

    private final UserRepository userRepository;

    public String generateToken(User user, int expirationDay, TokenType tokenType) {
        try {
            Date now = new Date();
            Instant expirationInstant = now.toInstant().plus(expirationDay, ChronoUnit.DAYS);
            Date expirationTime = Date.from(expirationInstant);

            JWSHeader header = new JWSHeader(tokenType == TokenType.ACCESS_TOKEN
                    ? JWSAlgorithm.HS256
                    : JWSAlgorithm.HS512);

            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("dev-white2077")
                    .issueTime(now)
                    .expirationTime(expirationTime)
                    .audience(user.getUsername())
                    .jwtID(UUID.randomUUID().toString())
                    .claim("avatar", user.getAvatar())
                    .claim("name", user.getName())
                    .claim("email", user.getEmail())
                    .claim("scope", buildScope(user))
                    .build();

            JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaimsSet.toJSONObject()));
            jwsObject.sign(new MACSigner(signerKey.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Failed to create JWT for user: {}", user.getUsername(), e);
            throw new AuthException(AuthError.UNAUTHORIZED);
        }
    }

    public TokenResponseDto refreshToken(String refreshToken) {
        try {
            log.info("Refreshing token: {}", refreshToken);

            // Decode the JWT and retrieve the subject (username)
            String username = decodeJwt(refreshToken, MacAlgorithm.HS512).getSubject();

            // Retrieve the user by username or throw an error if not found
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AuthException(AuthError.INVALID_USERNAME_OR_PASSWORD));

            // Generate a new access token
            String newAccessToken = generateToken(user, 1, TokenType.ACCESS_TOKEN);

            log.info("Token successfully refreshed for user: {}", username);

            return TokenResponseDto.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Error while refreshing token: {}", refreshToken, e);
            throw new AuthException(AuthError.INVALID_TOKEN);
        }
    }

    /**
     * Decodes a JWT token using the specified MAC algorithm. Verifies the token's integrity
     * based on the provided algorithm and extracts claims data.
     *
     * @param token     The JWT token to decode.
     * @param algorithm The MAC algorithm to use for decoding (e.g., HS256, HS512).
     * @return {@link Jwt} object representing the decoded token with user claims.
     */
    public Jwt decodeJwt(String token, MacAlgorithm algorithm) {

        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), algorithm.getName());

        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(algorithm)
                .build()
                .decode(token);

    }

    /**
     * Constructs the scope (roles or permissions) for the JWT token based on the user's role.
     *
     * @param user the user.
     * @return a space-separated string representing the user's scope.
     */
    private String buildScope(User user) {

        StringJoiner stringJoiner = new StringJoiner(" ");

        if (user.getRole() != null) {

            stringJoiner.add(user.getRole().name());

        }

        return stringJoiner.toString();

    }

    /**
     * Enum representing different types of tokens. Used to differentiate between
     * access tokens (for immediate API access) and refresh tokens (for renewing access tokens).
     */
    public enum TokenType {

        ACCESS_TOKEN,

        REFRESH_TOKEN
    }

    //    user if stored token in database or redis
//    public boolean introspectJWT(String token) throws JOSEException, ParseException {
//        boolean invalid = true;
//        try {
//            verifyToken(token);
//        } catch (RuntimeException e) {
//            invalid = false;
//        }
//        return invalid;
//    }

//    private void verifyToken(String token) throws JOSEException, ParseException {
//
//        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
//
//        SignedJWT signedJWT = SignedJWT.parse(token);
//
//        Date expiredDate = signedJWT.getJWTClaimsSet().getExpirationTime();
//
//        var verified = signedJWT.verify(verifier);
//        if (!(verified && expiredDate.after(new Date()))) {
//            throw new RuntimeException("String.valueOf(ErrorCode.UNAUTHENTICATED)");
//        }
//        //check token in database or redis
//    }

}

