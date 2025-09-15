package org.modelarium.auth.service

import mu.KotlinLogging
import org.apache.coyote.BadRequestException
import org.modelarium.auth.dto.AuthRs
import org.modelarium.auth.dto.LoginRq
import org.modelarium.auth.dto.RefreshRq
import org.modelarium.auth.dto.RegisterRq
import org.modelarium.auth.persistence.entity.UserEntity
import org.modelarium.auth.persistence.repository.UserRepository
import org.modelarium.auth.security.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class AuthServiceImpl @Autowired constructor(
    private val securityService: SecurityService,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository
): AuthService {
    private val log = KotlinLogging.logger {  }

    companion object {
        private val MIN_PASSWORD_LENGTH = 8
    }

    @Transactional
    override fun register(rq: RegisterRq): AuthRs {
        val username = rq.username.trim()
        val email = rq.email.trim()
        val password = rq.password

        validateInputs(username, password, email)

        if (userRepository.existsByUserName(username)) throw IllegalArgumentException()
        if (userRepository.existsByEmail(email)) throw IllegalArgumentException()

        val user = UserEntity(
            userName = username,
            email = email,
            passwordHash = passwordEncoder.encode(password),
            createdAt = OffsetDateTime.now()
        )

        try {
            userRepository.saveAndFlush(user)
        } catch (e: Exception) {
            log.warn(
                "DataIntegrityViolation while saving user" +
                    " (possible unique constraint) — " +
                    "username={}, email={}",
                username, email, e
            )
            when {
                userRepository.existsByUserName(username) -> throw IllegalArgumentException("username_taken")
                userRepository.existsByEmail(email) -> throw IllegalArgumentException("email_taken")
                else -> throw e
            }
        }

        val accessToken = securityService.generateAccessToken(username)
        val refreshToken = securityService.createAndStoreRefreshToken(user)

        log.info("User: [registered]: id=${user.id} , username=${user.userName}")
        return AuthRs(accessToken, refreshToken)
    }

    override fun login(rq: LoginRq): AuthRs {
        val user = userRepository.findByUserName(rq.username)
            ?: throw IllegalArgumentException()
        if (!passwordEncoder.matches(rq.password, user.passwordHash)) throw IllegalArgumentException()

        val accessToken = securityService.generateAccessToken(user.userName)
        val refreshToken = securityService.createAndStoreRefreshToken(user)

        log.info("User: [login]: id=${user.id} , username=${user.userName}")

        return AuthRs(accessToken, refreshToken)
    }

    override fun refresh(rq: RefreshRq): AuthRs {
        val rt = securityService.findRefreshTokenById(rq.tokenId) ?: throw IllegalArgumentException("invalid_refresh")
        if (rt.revoked || rt.expiresAt.isBefore(OffsetDateTime.now())) throw IllegalArgumentException("invalid_refresh")
        if (!passwordEncoder.matches(rq.tokenValue, rt.tokenHash)) throw IllegalArgumentException("invalid_refresh")
        rt.revoked = true
        securityService.saveRefreshToken(rt)
        val (newTokenId, newTokenValue) = securityService.createAndStoreRefreshToken(rt.user)
        val access = securityService.generateAccessToken(rt.user.userName)
        return AuthRs(access, newTokenId to newTokenValue)
    }

    override fun logout(rq: RefreshRq) {
        val refreshToken = securityService.findRefreshTokenById(rq.tokenId)
        refreshToken?.let {
            it.revoked = true
            securityService.saveRefreshToken(it)
        }
    }

    private fun validateInputs(
        username: String,
        password: String,
        email: String
    ) {
        if (username.isBlank()) throw BadRequestException()
        if (email.isBlank()) throw BadRequestException()
        if (password.length < MIN_PASSWORD_LENGTH) throw BadRequestException()
    }
}