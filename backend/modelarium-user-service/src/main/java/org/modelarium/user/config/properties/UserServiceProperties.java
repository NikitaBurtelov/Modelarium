package org.modelarium.user.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@ConfigurationProperties
@Getter
@Setter
public class UserServiceProperties {
    private UUID defaultAvatarKey;
}
