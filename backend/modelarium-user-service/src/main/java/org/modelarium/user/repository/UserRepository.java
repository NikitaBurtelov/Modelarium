package org.modelarium.user.repository;

import org.jspecify.annotations.NonNull;
import org.modelarium.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query(value = """
        SELECT *
        FROM users
        ORDER BY popularity_index DESC, sequence_id DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<UserEntity> findLatestTopUsers(int limit);

    @Query(value = """
        SELECT *
        FROM users
        WHERE sequence_id < :sequenceId
          AND id NOT IN :ids
        ORDER BY popularity_index DESC, sequence_id DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<UserEntity> findRefreshedTopUsers(List<UUID> ids, Long sequenceId, int limit);

    @Modifying
    @Query(value = """
        UPDATE users
        SET follow_count = follow_count + :count
        WHERE id = :userId
    """, nativeQuery = true)
    void incrementFollow(@Param("userId") UUID userId, @Param("count") long followerCount);

    List<UserEntity> findAllByIdIn(Collection<UUID> ids);

    Optional<UserEntity> findByEmail(String email);

    @NonNull Optional<UserEntity> findById(@NonNull UUID id);

    Optional<UserEntity> findByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);
}
