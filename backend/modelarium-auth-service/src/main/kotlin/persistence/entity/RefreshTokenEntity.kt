package org.modelarium.auth.persistence.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "refresh_tokens",
    indexes = [
        Index(name = "idx_refresh_token_token_id", columnList = "token_id"),
        Index(name = "idx_refresh_token_token_hash", columnList = "token_hash")
    ],
    uniqueConstraints = [
        UniqueConstraint(name = "uk_refresh_token_token_id", columnNames = ["token_id"])
    ]
)
class RefreshTokenEntity(
    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID = UUID.randomUUID(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,
    @Column(name = "token_id", columnDefinition = "uuid", nullable = false, unique = true)
    val tokenId: UUID = UUID.randomUUID(),
    @Column(name = "token_hash", length = 255, nullable = false)
    val tokenHash: String,
    @Column(name = "expires_at", nullable = false)
    val expiresAt: OffsetDateTime,
    @Column(name = "revoked")
    var revoked: Boolean = false
)