package org.modelarium.user.service.cache;

import java.util.Set;

public interface CacheCounterService {
    Long getValueAndDelete(String key);

    void increment(String key);

    void decrement(String key);

    Set<String> keysByPrefix(String keyPrefix);
}
