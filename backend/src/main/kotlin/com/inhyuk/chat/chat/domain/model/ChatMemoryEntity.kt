package com.inhyuk.chat.chat.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "chat_memory")
class ChatMemoryEntity (

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Int? = null,
    val chatSessionId: String,
    @Column(columnDefinition = "text")
    var messages : String = ""
){
}