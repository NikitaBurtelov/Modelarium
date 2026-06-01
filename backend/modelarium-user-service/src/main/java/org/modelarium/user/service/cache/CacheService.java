package org.modelarium.user.service.cache;

import org.modelarium.user.dto.CacheEntity;

import java.util.List;
import java.util.Set;

public interface CacheService<T extends CacheEntity> {
    public  Object getValue(String key);

    List<T> getValues(
            String key,
            int start,
            int end
    );

    void addValue(String key, T value);

    void addValues(String key, List<T> value);

    public void setValue(String key, T value);
    public void setValues(String key, List<T> value);
    public void delete(String key);

    boolean exists(String key);

    boolean availableRange(String key, int positionId);

    Set<String> keysByPrefix(String keyPrefix);
}
