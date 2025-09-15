package org.modelarium.auth.controller

import org.modelarium.auth.dto.*
import org.modelarium.auth.service.AuthService
import org.modelarium.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController @Autowired constructor(
    private val authService: AuthService,
    private val userService: UserService
) {
    @PostMapping("/register")
    fun register(@RequestBody rq: RegisterRq): ResponseEntity<AuthRs> {
        val res = authService.register(rq)
        return ResponseEntity.ok(res)
    }

    @PostMapping("/login")
    fun login(@RequestBody rq: LoginRq): ResponseEntity<AuthRs> {
        val res = authService.login(rq)
        return ResponseEntity.ok(res)
    }

    @PostMapping
    fun refresh(@RequestBody rq: RefreshRq): ResponseEntity<AuthRs> {
        val res = authService.refresh(rq)
        return ResponseEntity.ok(res)
    }

    @PostMapping("/logout")
    fun logout(@RequestBody req: RefreshRq): ResponseEntity<Void> {
        authService.logout(req)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/info")
    fun personalInfo(
        @AuthenticationPrincipal principal: UserDetails
    ): ResponseEntity<UserProfileRs> {
        val user = userService.findByUsername(principal.username)
            ?: return ResponseEntity.notFound().build<UserProfileRs>()
        return ResponseEntity.ok(
            UserProfileRs(
                id = user.id,
                username = user.userName,
                email = user.email,
                displayName = user.displayName,
                bio = user.bio
            )
        )
    }
}