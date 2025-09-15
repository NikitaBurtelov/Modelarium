package org.modelarium.auth.service

import org.modelarium.auth.dto.AuthRs
import org.modelarium.auth.dto.LoginRq
import org.modelarium.auth.dto.RefreshRq
import org.modelarium.auth.dto.RegisterRq

sealed interface AuthService {
    fun register(rq: RegisterRq): AuthRs
    fun login(rq: LoginRq): AuthRs
    fun refresh(rq: RefreshRq): AuthRs
    fun logout(rq: RefreshRq)
}