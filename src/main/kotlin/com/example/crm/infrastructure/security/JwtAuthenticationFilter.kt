package com.example.crm.infrastructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(private val jwtService: JwtService) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            try {
                val token = header.substringAfter("Bearer ").trim()
                val subject = jwtService.parseSubject(token)
                // For now, no roles: grant a basic user authority
                val auth = UsernamePasswordAuthenticationToken(subject, null, listOf(SimpleGrantedAuthority("ROLE_USER")))
                SecurityContextHolder.getContext().authentication = auth
            } catch (ex: Exception) {
                // invalid token -> clear context
                SecurityContextHolder.clearContext()
            }
        }
        filterChain.doFilter(request, response)
    }
}
