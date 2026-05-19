package org.modelarium.user.config;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.modelarium.user.config.properties.WebProperties;
import org.modelarium.user.dto.CacheEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class UserServiceConfig {
    private final WebProperties webProperties;

    @Bean
    public WebClient mediaWebClient() {
        return WebClient.builder()
                .baseUrl(webProperties.getMediaBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .responseTimeout(Duration.ofSeconds(5))
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        )
                ).build();
    }

    @Bean
    public WebClient postWebClient() {
        return WebClient.builder()
                .baseUrl(webProperties.getPostBaseUrl())
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .responseTimeout(Duration.ofSeconds(5))
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        )
                ).build();
    }

    @Bean
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl(webProperties.getPostBaseUrl())
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient.create()
                                        .responseTimeout(Duration.ofSeconds(5))
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        )
                ).build();
    }

    @Bean
    public RedisTemplate<String, CacheEntity> redisTemplate(
            RedisConnectionFactory redisConnectionFactory
    ) {
        var redisTemplate = new RedisTemplate<String, CacheEntity>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(
                new GenericJackson2JsonRedisSerializer()
        );

        return redisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager(
            RedisConnectionFactory redisConnectionFactory
    ) {

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(
                                                new GenericJackson2JsonRedisSerializer()
                                        )
                        );

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
}