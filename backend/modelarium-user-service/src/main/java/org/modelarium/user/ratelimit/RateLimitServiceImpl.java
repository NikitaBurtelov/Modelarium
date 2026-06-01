package org.modelarium.user.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.modelarium.user.ratelimit.filter.RateLimitPolicy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {
    ProxyManager<String> proxyManager;

    @Override
    public ConsumptionProbe tryConsume(
            String key,
            RateLimitPolicy policy
    ) {
        Bucket bucket = proxyManager.builder().build(
                policy.name() + ":" + key,
                () -> buildBucketConfig(policy)
        );

        return bucket.tryConsumeAndReturnRemaining(1);
    }

    private BucketConfiguration buildBucketConfig(RateLimitPolicy policy) {
        var capacity = policy.getCapacity();
        var duration = policy.getDuration();

        return BucketConfiguration.builder()
                .addLimit(
                        Bandwidth.builder().
                                capacity(capacity)
                                .refillGreedy(capacity,duration)
                                .build()
                ).build();
    }
}
