package org.modelarium.auth.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "user", schema = "public")
class UserEntity(
    @Id
    @Column(columnDefinition = "uuid")
    val id: UUID = UUID.randomUUID(),
    @Column(name = "username", unique = true, nullable = false)
    var userName: String,
    @Column(unique = true, nullable = false)
    var email: String,
    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,
    @Column(name = "display_name")
    var displayName: String? = null,
    @Column(name = "avatar_key")
    var avatarKey: String? = null,
    @Column(name = "bio")
    var bio: String? = null,
    @Column(name = "email_verified")
    var emailVerified: Boolean = false,
    @Column(name = "created_at")
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    @Column(name = "updated_at")
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
)