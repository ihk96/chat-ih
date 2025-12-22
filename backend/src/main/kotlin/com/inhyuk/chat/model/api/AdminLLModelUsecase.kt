package com.inhyuk.chat.model.api

import com.inhyuk.chat.model.api.dto.AdminModelRequestDto
import com.inhyuk.chat.model.api.dto.AdminModelResponseDto
import com.inhyuk.chat.model.domain.LLModelEntity
import com.inhyuk.chat.model.domain.LLModelRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminLLModelUsecase(
    private val llModelRepository: LLModelRepository
){

    fun getModels() : List<AdminModelResponseDto> {
        return llModelRepository.findAll().map { AdminModelResponseDto(it) }
    }

    fun getModel(id: String) : AdminModelResponseDto {
        return llModelRepository.findById(id).map { AdminModelResponseDto(it) }.orElseThrow { IllegalArgumentException("Model not found") }
    }

    @Transactional
    fun createModel(request: AdminModelRequestDto) : AdminModelResponseDto {
        val entity = LLModelEntity(
            publicName = request.publicName,
            originName = request.originName,
            providerId = request.providerId,
            completionUrl = request.completionUrl
        )
        return AdminModelResponseDto(llModelRepository.save(entity))
    }

    @Transactional
    fun updateModel(id: String, request: AdminModelRequestDto) : AdminModelResponseDto {
        val model = llModelRepository.findById(id).orElseThrow { IllegalArgumentException("Model not found") }

        model.originName = request.originName
        model.publicName = request.publicName
        model.providerId = request.providerId
        model.completionUrl = request.completionUrl

        return AdminModelResponseDto(model)
    }

    @Transactional
    fun deleteModel(id: String) {
        if(!llModelRepository.existsById(id)){
            throw IllegalArgumentException("Model not found")
        }
        llModelRepository.deleteById(id)
    }
}