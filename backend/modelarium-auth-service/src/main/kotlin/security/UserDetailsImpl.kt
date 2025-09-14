package org.modelarium.auth.security

import org.modelarium.auth.persistence.entity.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    private val user: UserEntity
): UserDetails {
    //TODO
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf()

    override fun getPassword() = user.passwordHash

    override fun getUsername() = user.userName

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    fun id() = user.id
    fun email() = user.email
    fun displayName() = user.displayName
}