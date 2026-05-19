package org.modelarium.user.repository;

import org.modelarium.user.model.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRepository extends JpaRepository<FollowEntity, UUID> {
}
