package com.inhyuk.chat.prompt.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "prompts")
class PromptEntity(

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    var id: Long? = null,
    var name: String? = null,
    var description: String? = null,
    var purpose: PromptPurpose? = null,
    @Column(columnDefinition = "TEXT")
    var prompt : String? = null
) {
}