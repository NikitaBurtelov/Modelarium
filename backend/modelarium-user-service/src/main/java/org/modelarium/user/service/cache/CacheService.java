package org.modelarium.user.service.cache;

import org.modelarium.user.dto.CacheEntity;

import java.util.List;

public interface CacheService<T extends CacheEntity> {
    public  Object getValue(String key);

    List<T> getValues(
            String key,
            int start,
            int end
    );

    public void setValue(String key, T value);
    public void setValues(String key, List<T> value);
    public void delete(String key);

    boolean exists(String key);

    boolean availableRange(String key, int positionId);
}
