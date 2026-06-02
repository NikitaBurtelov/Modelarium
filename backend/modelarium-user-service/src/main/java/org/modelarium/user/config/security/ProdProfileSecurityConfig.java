package org.modelarium.user.config.security;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.security.converter.JwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Profile("prod")
@Configuration
@RequiredArgsConstructor
public class ProdProfileSecurityConfig {
    private final JwtConverter jwtConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/public").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtConverter
                                )
                        )
                )
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object realmAccessObj = jwt.getClaim("realm_access");

            if (!(realmAccessObj instanceof Map<?, ?> realmAccess)) {
                return Set.of();
            }

            Object rolesObj = realmAccess.get("roles");

            if (!(rolesObj instanceof List<?> roles)) {
                return Set.of();
            }

            return roles.stream()
                    .map(role ->
                            new SimpleGrantedAuthority(
                                    "ROLE_" + role
                            )
                    )
                    .collect(Collectors.toSet());
        });

        return converter;
    }
}