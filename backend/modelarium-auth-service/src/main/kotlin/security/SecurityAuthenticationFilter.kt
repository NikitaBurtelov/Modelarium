package org.modelarium.auth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityAuthenticationFilter @Autowired constructor(
    private val securityService: SecurityService,
    private val userDetailsService: UserDetailsService
): OncePerRequestFilter() {
    val log = KotlinLogging.logger {  }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (!authHeader.isNullOrBlank() && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            try {
                if (securityService.validate(token)) {
                    val userName = securityService.parseSubject(token)
                    val userDetails = userDetailsService.loadUserByUsername(userName)
                    val auth = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )
                    auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = auth
                }
            } catch (e: Exception) {
                log.info { "Token is uncorrected" }
            }
            filterChain.doFilter(request, response)
        }
    }
}