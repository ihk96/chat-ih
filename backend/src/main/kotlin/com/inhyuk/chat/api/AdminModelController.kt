package com.inhyuk.chat.api

import com.inhyuk.chat.api.dto.AdminModelRequestDto
import com.inhyuk.chat.api.dto.AdminModelResponseDto
import com.inhyuk.chat.usecase.AdminLLModelUsecase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/models")
class AdminModelController(
    private val llModelUsecase : AdminLLModelUsecase
) {

    @GetMapping()
    fun getModels() : ResponseEntity<List<AdminModelResponseDto>> {
        val modelList = llModelUsecase.getModels()
        return ResponseEntity.ok(modelList)
    }

    @PostMapping()
    fun postModel(@RequestBody request: AdminModelRequestDto) : ResponseEntity<AdminModelResponseDto> {
        return ResponseEntity.ok(llModelUsecase.createModel(request))
    }

    @PutMapping("/{id}")
    fun putModel(@PathVariable id: String, @RequestBody request: AdminModelRequestDto) : ResponseEntity<AdminModelResponseDto> {
        return ResponseEntity.ok(llModelUsecase.updateModel(id, request))
    }

    @DeleteMapping("/{id}")
    fun deleteModel(@PathVariable id: String) : ResponseEntity<Void> {
        llModelUsecase.deleteModel(id)
        return ResponseEntity.ok().build()
    }

}