package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        RegisterRequest registerRequest = getUserDetails(token);
        if (registerRequest == null || registerRequest.getEmail() == null) {
            log.warn("Could not parse user details or email from token.");
            return chain.filter(exchange);
        }

        String email = registerRequest.getEmail();
        String keycloakId = registerRequest.getKeycloakId();

        return userService.validateUser(email)
                .flatMap(exists -> {
                    if (!exists) {
                        log.info("User with email {} not found. Registering...", email);
                        return userService.registerUser(registerRequest);
                    } else {
                        // User already exists, skip registration.
                        log.info("User with email {} already exists. Skipping sync.", email);
                        return Mono.empty(); // Just signal completion
                    }
                })
                .then(Mono.defer(() -> {
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-ID", keycloakId)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                }))
                .onErrorResume(e -> {
                    log.error("Error during user sync for email {}: {}", email, e.getMessage());
                    return chain.filter(exchange);
                });
    }

    private RegisterRequest getUserDetails(String token) {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));
            registerRequest.setKeycloakId(claims.getStringClaim("sub"));
            registerRequest.setPassword("dummy@123123");
            registerRequest.setFirstName(claims.getStringClaim("given_name"));
            registerRequest.setLastName(claims.getStringClaim("family_name"));
            return registerRequest;
        } catch (Exception e) {
            log.error("Failed to parse JWT: {}", e.getMessage());
            return null;
        }
    }
}
