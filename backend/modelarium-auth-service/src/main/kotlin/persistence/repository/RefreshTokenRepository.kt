package org.modelarium.auth.persistence.repository

import org.modelarium.auth.persistence.entity.RefreshTokenEntity
import org.modelarium.auth.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, UUID> {
    fun findByTokenId(tokenId: UUID): RefreshTokenEntity?
    fun findByUser(user: UserEntity): List<RefreshTokenEntity>
    fun findByTokenHash(tokenHash: String): RefreshTokenEntity?
    fun deleteByUser(user: UserEntity): Long
}