package com.inhyuk.chat.chat.api.dto

import com.inhyuk.chat.chat.domain.model.ChatSession

data class ChatSessionDto (
    private val session : ChatSession
) {
    val id : String = session.id
    val userId : String = session.userId
    val title: String = session.entity.title
    val messages = session.messages.toMutableList()
}