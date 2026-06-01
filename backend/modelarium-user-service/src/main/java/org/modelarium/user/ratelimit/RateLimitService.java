package org.modelarium.user.ratelimit;

import io.github.bucket4j.ConsumptionProbe;
import org.modelarium.user.ratelimit.filter.RateLimitPolicy;

public interface RateLimitService {
    ConsumptionProbe tryConsume(
            String key,
            RateLimitPolicy policy
    );
}
