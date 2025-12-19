package com.inhyuk.chat.chat.api.dto

data class StreamEventDto (
    val type: String? = null, // "token", "function_call", "function_result", "error", "done"
    val content: String? = null,
    val metadata: MutableMap<String?, Any?>? = null, // constructor, getters
)