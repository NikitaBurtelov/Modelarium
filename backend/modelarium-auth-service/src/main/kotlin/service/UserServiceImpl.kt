package org.modelarium.auth.service

import org.modelarium.auth.persistence.entity.UserEntity
import org.modelarium.auth.persistence.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository
): UserService {
    override fun findById(id: UUID): UserEntity {
        return try {
            userRepository.findById(id).get()
        } catch (e: Exception) {
            throw NotFoundException()
        }
    }

    override fun findByEmail(email: String): UserEntity? {
        return try {
            userRepository.findByEmail(email)
        } catch (e: Exception) {
            throw NotFoundException()
        }
    }

    override fun findByUsername(name: String): UserEntity? {
        return try {
            userRepository.findByUserName(name)
        } catch (e: Exception) {
            throw NotFoundException()
        }
    }

    @Transactional
    fun createUser(userName: String, email: String, passwordHash: String) {
        userRepository.save(
            UserEntity(
                userName = userName,
                email = email,
                passwordHash = passwordHash
            )
        )
    }

    @Transactional
    fun updateUser(user: UserEntity, displayName: String?, bio: String?) {
        userRepository.save(
            user.apply {
                this.bio = bio
                this.displayName = displayName
            }
        )
    }
}