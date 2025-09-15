package org.modelarium.auth.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.modelarium.auth.persistence.entity.RefreshTokenEntity
import org.modelarium.auth.persistence.entity.UserEntity
import org.modelarium.auth.persistence.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Key
import java.security.SecureRandom
import java.time.OffsetDateTime
import java.util.*

@Service
class SecurityServiceImpl @Autowired constructor(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-exp-ms}") private val accessExpMs: Long,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
): SecurityService {
    private val key: Key = Keys.hmacShaKeyFor(secret.toByteArray())

    override fun generateAccessToken(subject: String, claims: Map<String, Any>): String {
        return Jwts.builder()
            .setSubject(subject)
            .addClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessExpMs))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    @Transactional
    override fun findRefreshTokenById(tokenId: UUID): RefreshTokenEntity? {
        return  refreshTokenRepository.findByTokenId(tokenId)
    }

    @Transactional
    override fun saveRefreshToken(refreshTokenEntity: RefreshTokenEntity) {
        refreshTokenRepository.save(refreshTokenEntity)
    }

    @Transactional
    override fun createAndStoreRefreshToken(user: UserEntity): Pair<UUID, String> {
        val tokenId = UUID.randomUUID()
        val tokenValue = generateSecureRandomString()
        val tokenHash = passwordEncoder.encode(tokenValue)
        //TODO вынести кол-во дней
        val expiresAt = OffsetDateTime.now().plusDays(30)
        val refreshToken = RefreshTokenEntity(
            tokenId = tokenId,
            user = user,
            tokenHash = tokenHash,
            expiresAt = expiresAt
        )
        refreshTokenRepository.save(refreshToken)
        return tokenId to tokenValue
    }

    override fun parseSubject(token: String): String {
        return run{
            Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
        }.body.subject
    }

    override fun validate(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun generateSecureRandomString(lenBytes: Int = 64): String {
        val bytes = ByteArray(lenBytes)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}