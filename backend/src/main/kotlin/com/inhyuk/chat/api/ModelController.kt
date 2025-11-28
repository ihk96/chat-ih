package com.inhyuk.chat.api

import com.inhyuk.chat.api.dto.ModelResponseDto
import com.inhyuk.chat.api.dto.toModelResponseDto
import com.inhyuk.chat.usecase.BasicLLModelUsecase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/models")
class ModelController(
    private val llModelUsecase: BasicLLModelUsecase,
) {

    @GetMapping()
    fun getModels() : ResponseEntity<List<ModelResponseDto>> {
        val modelList = llModelUsecase.getModels().map { it.toModelResponseDto() }
        return ResponseEntity.ok(modelList)
    }
}