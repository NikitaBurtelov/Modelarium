package org.modelarium.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder(builderMethodName = "builder")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "sequence_id", updatable = false, nullable = false, unique = true)
    private Long sequenceId;
    @Column(name = "username", unique = true, nullable = false)
    private String userName;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "avatar_key")
    private String avatarKey;
    @Column(name = "bio")
    private String bio;
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false, updatable = false)
    private OffsetDateTime updateAt;
    @Column(name = "popularity_index", nullable = false, updatable = true)
    private int popularityIndex;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
            popularityIndex = 0;
        }
        createdAt = OffsetDateTime.now();
        updateAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = OffsetDateTime.now();
    }
}
