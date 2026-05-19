package org.modelarium.user.service.cache;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.config.properties.CacheProperties;
import org.modelarium.user.dto.CacheEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl<T extends CacheEntity> implements CacheService<T> {
    private final RedisTemplate<String, T> redisTemplate;
    private final CacheProperties cacheProperties;

    @Override
    public T getValue(String key) {
        return redisTemplate.opsForValue().get((key));
    }

    @Override
    public List<T> getValues(
            String key,
            int start,
            int end
    ) {
        List<T> result = redisTemplate.opsForList()
                .range(key, start, end);

        return result == null ? Collections.emptyList() : result;
    }

    @Override
    public void setValue(String key, T value) {
        redisTemplate.opsForValue().set(key, value);

        redisTemplate.expire(key, cacheProperties.getFeedTtl(), TimeUnit.MINUTES);
    }

    @Override
    public void setValues(String key, List<T> value) {
        redisTemplate.opsForList().rightPushAll(key, value);

        redisTemplate.expire(key, cacheProperties.getFeedTtl(), TimeUnit.MINUTES);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public boolean availableRange(String key, int positionId) {
        return positionId <= size(key);
    }

    private long size(String key) {
        var size = redisTemplate.opsForList().size(key);

        if (size == null) {
            throw new NullPointerException();
        }
        return size;
    }

    private String buildKey(UUID id) {
        return "feed:users:" + id;
    }
}
