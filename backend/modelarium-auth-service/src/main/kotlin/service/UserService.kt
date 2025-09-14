package org.modelarium.auth.service

import org.modelarium.auth.persistence.entity.UserEntity
import java.util.UUID

interface UserService {
    fun findById(id: UUID): UserEntity
    fun findByEmail(email: String): UserEntity?
    fun findByUsername(name: String): UserEntity?
}