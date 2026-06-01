package org.modelarium.user.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.RequiredArgsConstructor;
import org.modelarium.user.dto.CacheEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {
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
    public RedisTemplate<String, Long> redisTemplateCounter(
            RedisConnectionFactory redisConnectionFactory
    ) {
        var redisTemplateCounter = new RedisTemplate<String, Long>();

        redisTemplateCounter.setConnectionFactory(redisConnectionFactory);
        redisTemplateCounter.setKeySerializer(new StringRedisSerializer());
        redisTemplateCounter.setValueSerializer(
                new GenericJackson2JsonRedisSerializer()
        );

        return redisTemplateCounter;
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

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient(
            LettuceConnectionFactory lettuceConnectionFactory
    ) {

        RedisURI redisUri = RedisURI.builder()
                .withHost(lettuceConnectionFactory.getHostName())
                .withPort(lettuceConnectionFactory.getPort())
                .build();

        return RedisClient.create(redisUri);
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, byte[]> redisConnection(
            RedisClient redisClient
    ) {
        return redisClient.connect(
                RedisCodec.of(
                        StringCodec.UTF8,
                        ByteArrayCodec.INSTANCE
                )
        );
    }

    @Bean
    public ProxyManager<String> proxyManager(
            StatefulRedisConnection<String, byte[]> redisConnection
    ) {
        return Bucket4jLettuce.casBasedBuilder(redisConnection).build();
    }
}
