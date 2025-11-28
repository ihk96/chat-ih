package com.inhyuk.chat.domain.model.llm

import com.inhyuk.chat.domain.model.ModelProvider
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "ll_model")
class LLModel(

    @Id
    val id : String,
    val publicName : String,
    val originName : String,
    val provider : ModelProvider,
    val baseUrl : String,
    val apiKey : String,
    val completionUrl : String,

    )