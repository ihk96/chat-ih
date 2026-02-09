package com.inhyuk.chat.user.api

import com.inhyuk.chat.user.api.dto.SignupRequest
import com.inhyuk.chat.user.api.dto.UpdateUserRequest
import com.inhyuk.chat.user.api.dto.UserResponse
import com.inhyuk.chat.user.domain.Role
import com.inhyuk.chat.user.domain.UserService
import com.inhyuk.chat.user.security.CustomUserDetails
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<UserResponse> {
        val user = userService.register(request.username, request.password)
        return ResponseEntity.ok(UserResponse.from(user))
    }

    @GetMapping
    fun list(authentication: Authentication): ResponseEntity<List<UserResponse>> {
        val requester = authentication.principal as CustomUserDetails
        val roles = Role.fromAuthorities(requester.authorities.map { it.authority })
        if (!roles.contains(Role.ADMIN)) {
            return ResponseEntity.status(403).build()
        }
        val users = userService.listUsers().map { UserResponse.from(it) }
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<UserResponse> {
        val requester = authentication.principal as CustomUserDetails
        val roles = Role.fromAuthorities(requester.authorities.map { it.authority })
        if (!roles.contains(Role.ADMIN) && requester.id != id) {
            return ResponseEntity.status(403).build()
        }
        val user = userService.getUser(id)
        return ResponseEntity.ok(UserResponse.from(user))
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody request: UpdateUserRequest,
        authentication: Authentication
    ): ResponseEntity<UserResponse> {
        val requester = authentication.principal as CustomUserDetails
        val roles = Role.fromAuthorities(requester.authorities.map { it.authority })
        val user = userService.updateUser(
            id = id,
            requesterId = requester.id,
            requesterRoles = roles,
            newPassword = request.password,
            newRoles = request.roles
        )
        return ResponseEntity.ok(UserResponse.from(user))
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val requester = authentication.principal as CustomUserDetails
        val roles = Role.fromAuthorities(requester.authorities.map { it.authority })
        userService.deleteUser(id, requester.id, roles)
        return ResponseEntity.ok().build()
    }
}
