package com.inhyuk.chat.user.facade

import com.inhyuk.chat.user.domain.UserRepository
import com.inhyuk.chat.user.facade.dto.UserDTO
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userRepository: UserRepository
) {

    fun getUserById(id: String) : UserDTO? {
        val userEntity = userRepository.findById(id)
        if(userEntity.isEmpty) return null
        return UserDTO.fromEntity(userEntity.get())
    }
}