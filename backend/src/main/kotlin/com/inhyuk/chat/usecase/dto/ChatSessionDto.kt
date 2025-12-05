package com.inhyuk.chat.usecase.dto

import com.inhyuk.chat.domain.chat.model.ChatSession


data class ChatSessionDto (
    private val session : ChatSession
) {
    val id : String = session.id
    val userId : String = session.userId
    val messages = session.messages.toMutableList()
}