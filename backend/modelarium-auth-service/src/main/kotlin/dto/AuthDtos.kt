package org.modelarium.auth.dto

import java.util.*

data class RegisterRq(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRq(
    val username: String,
    val password: String
)

data class RefreshRq(
    val tokenValue: String,
    val tokenId: UUID
)

data class AuthRs(
    val accessToken: String,
    val refreshToken: Pair<UUID, String>?
)

data class UserProfileRs(
    val id: UUID,
    val username: String,
    val email: String,
    val displayName: String?,
    val bio: String?
)