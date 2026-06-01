package org.modelarium.user.repository;

import org.modelarium.user.model.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<FollowEntity, UUID> {
    boolean existsByUserIdAndSubscriberId(UUID userId, UUID subscriberId);

    void deleteByUserIdAndSubscriberId(UUID userId, UUID subscriberId);
}
