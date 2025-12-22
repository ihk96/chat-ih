package com.inhyuk.chat.model.api

import com.inhyuk.chat.common.controller.RestResponse
import com.inhyuk.chat.model.api.dto.AdminModelRequestDto
import com.inhyuk.chat.model.api.dto.AdminModelResponseDto
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/models")
class AdminModelController(
    private val llModelUsecase : AdminLLModelUsecase
) {

    @GetMapping()
    fun getModels() : RestResponse<List<AdminModelResponseDto>> {
        val modelList = llModelUsecase.getModels()
        return RestResponse.ok(modelList)
    }
    @GetMapping("/{id}")
    fun getModel(@PathVariable id: String) : RestResponse<AdminModelResponseDto> {
        return RestResponse.ok(llModelUsecase.getModel(id))
    }

    @PostMapping()
    fun postModel(@RequestBody request: AdminModelRequestDto) : RestResponse<AdminModelResponseDto> {
        return RestResponse.ok(llModelUsecase.createModel(request))
    }

    @PutMapping("/{id}")
    fun putModel(@PathVariable id: String, @RequestBody request: AdminModelRequestDto) : RestResponse<AdminModelResponseDto> {
        return RestResponse.ok(llModelUsecase.updateModel(id, request))
    }

    @DeleteMapping("/{id}")
    fun deleteModel(@PathVariable id: String) : RestResponse<Unit> {
        llModelUsecase.deleteModel(id)
        return RestResponse.ok(Unit)
    }
}