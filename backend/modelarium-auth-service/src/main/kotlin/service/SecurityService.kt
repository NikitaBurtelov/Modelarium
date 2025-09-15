package org.modelarium.auth.service

import org.modelarium.auth.persistence.entity.RefreshTokenEntity
import org.modelarium.auth.persistence.entity.UserEntity
import java.util.*

sealed interface SecurityService {
    fun generateAccessToken(
        subject: String,
        claims: Map<String, Any> = emptyMap()
    ): String

    fun  createAndStoreRefreshToken(user: UserEntity): Pair<UUID, String>

    fun parseSubject(token: String): String

    fun validate(token: String): Boolean

    fun findRefreshTokenById(tokenId: UUID): RefreshTokenEntity?

    fun saveRefreshToken(refreshTokenEntity: RefreshTokenEntity)
}