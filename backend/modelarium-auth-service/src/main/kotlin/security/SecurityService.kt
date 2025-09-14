package org.modelarium.auth.security

interface SecurityService {
    fun generateAccessToken(
        subject: String,
        claims: Map<String, Any> = emptyMap()
    ): String

    fun parseSubject(token: String): String

    fun validate(token: String): Boolean
}