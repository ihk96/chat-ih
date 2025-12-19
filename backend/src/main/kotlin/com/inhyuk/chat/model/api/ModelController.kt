package com.inhyuk.chat.model.api

import com.inhyuk.chat.common.controller.RestResponse
import com.inhyuk.chat.model.api.dto.ModelWithProviderResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/models")
class ModelController(
    private val llModelUsecase: BasicLLModelUsecase,
) {

    @GetMapping()
    fun getModels() : RestResponse<List<ModelWithProviderResponseDto>> {
        return RestResponse.ok(llModelUsecase.getModels())
    }
}