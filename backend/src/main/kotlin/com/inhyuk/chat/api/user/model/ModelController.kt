package com.inhyuk.chat.api.user.model

import com.inhyuk.chat.api.user.model.dto.ModelResponseDto
import com.inhyuk.chat.api.user.model.dto.toModelResponseDto
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