package org.modelarium.auth.service

import org.modelarium.auth.persistence.repository.UserRepository
import org.modelarium.auth.security.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl @Autowired constructor(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUserName(username)
            ?: throw UsernameNotFoundException("User not found: $username")
        return UserDetailsImpl(user)
    }
}