package org.modelarium.auth.persistence.repository

import org.modelarium.auth.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository: JpaRepository<UserEntity, UUID> {
    fun findByUserName(userName: String): UserEntity?
    fun findByEmail(email: String): UserEntity?
    fun existsByUserName(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}