package org.modelarium.user.repository;

import org.jspecify.annotations.NonNull;
import org.modelarium.user.model.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    List<UserEntity> findAllByIdIn(Collection<UUID> ids);
    Optional<UserEntity> findByEmail(String email);
    @NonNull Optional<UserEntity> findById(@NonNull UUID id);
    Optional<UserEntity> findByUserName(String userName);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
}
