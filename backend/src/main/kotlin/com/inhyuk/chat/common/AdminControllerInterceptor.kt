package com.inhyuk.chat.common

import com.inhyuk.chat.user.domain.Role
import com.inhyuk.chat.user.security.CustomUserDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AdminControllerInterceptor : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (!request.requestURI.startsWith("/api/admin")) {
            return true
        }

        val authentication = SecurityContextHolder.getContext().authentication
        val userDetais = authentication.principal as CustomUserDetails
        if (!userDetais.isAdmin()) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            return false
        }
        return true
    }
}
