package com.inhyuk.chat.common.controller

import org.springframework.http.ResponseEntity

class RestResponse<T>(
    val data: T? = null,
    val code: String? = "0",
    val message: String? = null
) {
    companion object {
        fun <T> ok(data: T, code: String = "0"): RestResponse<T> = RestResponse(data = data, code = code)
        fun <T> error(message: String, code: String? = null): RestResponse<T> = RestResponse(code = code, message = message)
    }

    fun toResponseEntity(): ResponseEntity<RestResponse<T>> {
        return ResponseEntity.ok(this)
    }
}