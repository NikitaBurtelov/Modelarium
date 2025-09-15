package org.modelarium.auth.service

import org.modelarium.auth.persistence.entity.UserEntity
import java.util.UUID

interface UserService {
    fun findById(id: UUID): UserEntity
    fun findByEmail(email: String): UserEntity?
    fun findByUsername(name: String): UserEntity?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun createUser(userName: String, email: String, passwordHash: String)
    fun saveAndFlush(user: UserEntity)
    fun updateUser(user: UserEntity, displayName: String?, bio: String?)
}