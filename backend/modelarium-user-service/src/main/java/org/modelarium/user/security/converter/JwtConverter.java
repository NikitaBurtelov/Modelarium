package org.modelarium.user.security.converter;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.security.model.AuthUserData;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt value) {
        UUID userId = UUID.fromString(
                value.getClaim("user_id")
        );
        String userName = value.getClaim("user_name");

        Object realmAccessObj = value.getClaim("realm_access");

        if (!(realmAccessObj instanceof Map<?, ?> realmAccess)) {
            realmAccessObj = Map.of();
        }

        Object rolesObj = ((Map<?, ?>) realmAccessObj).get("roles");

        if (!(rolesObj instanceof List<?> roles)) {
            rolesObj = List.of();
        }

        Set<GrantedAuthority> authorities = ((List<?>) rolesObj).stream()
                .map(role ->
                        new SimpleGrantedAuthority("ROLE_" + role)
                )
                .collect(Collectors.toSet());

        return new UsernamePasswordAuthenticationToken(
                new AuthUserData(
                        userId,
                        userName,
                        Set.copyOf((List<String>) rolesObj)
                ),
                null,
                authorities
        );
    }
}
