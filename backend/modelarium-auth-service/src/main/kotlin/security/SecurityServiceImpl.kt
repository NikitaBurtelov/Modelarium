package org.modelarium.auth.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class SecurityServiceImpl @Autowired constructor(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-exp-ms}") private val accessExpMs: Long
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
}