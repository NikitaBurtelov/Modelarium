package org.modelarium.post.config;

import lombok.RequiredArgsConstructor;
import org.modelarium.post.config.properties.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class PostServiceConfig {
    private final WebProperties webProperties;

    @Bean
    public WebClient mediaWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(webProperties.getMediaBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
