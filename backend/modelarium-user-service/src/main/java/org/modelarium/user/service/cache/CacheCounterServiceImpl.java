package org.modelarium.user.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CacheCounterServiceImpl implements CacheCounterService{
    private final RedisTemplate<String, Long> redisTemplateCounter;

    @Override
    public Long getValueAndDelete(String key) {
        return redisTemplateCounter.opsForValue().getAndDelete(key);
    }

    @Override
    public void increment(String key) {
        redisTemplateCounter.opsForValue().increment(key);
    }

    @Override
    public void decrement(String key) {
        redisTemplateCounter.opsForValue().decrement(key);
    }

    @Override
    public Set<String> keysByPrefix(String keyPrefix) {
        Set<String> keys = redisTemplateCounter.keys(keyPrefix);

        return keys != null ? keys : Collections.emptySet();
    }
}
