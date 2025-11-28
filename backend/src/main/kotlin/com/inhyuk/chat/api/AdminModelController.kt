package com.inhyuk.chat.api

import com.inhyuk.chat.domain.model.llm.LLModel
import com.inhyuk.chat.usecase.AdminLLModelUsecase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/models")
class AdminModelController(
    private val llModelUsecase : AdminLLModelUsecase
) {

    @GetMapping()
    fun getModels() : ResponseEntity<List<LLModel>> {
        val modelList = llModelUsecase.getModels()
        return ResponseEntity.ok(modelList)
    }

    @PostMapping()
    fun postModel() : ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @PutMapping()
    fun putModel() : ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

    @DeleteMapping()
    fun deleteModel() : ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

}