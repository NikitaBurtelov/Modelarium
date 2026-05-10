package org.modelarium.user.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "web")
@Setter
@Getter
public class WebProperties {
    private String mediaBaseUrl;
    private String postBaseUrl;
    private String notificationBaseUrl;
}