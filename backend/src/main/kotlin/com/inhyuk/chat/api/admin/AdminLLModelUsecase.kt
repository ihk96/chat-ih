package com.inhyuk.chat.api.admin

import com.inhyuk.chat.api.admin.dto.AdminModelRequestDto
import com.inhyuk.chat.api.admin.dto.AdminModelResponseDto
import com.inhyuk.chat.domain.model.llm.LLModelRepository
import com.inhyuk.chat.domain.model.llm.LLModelService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminLLModelUsecase(
    private val llModelRepository: LLModelRepository,
    private val llMmodelService: LLModelService
){

    fun getModels() : List<AdminModelResponseDto> {
        return llModelRepository.findAll().map { AdminModelResponseDto(it) }
    }

    @Transactional
    fun createModel(request: AdminModelRequestDto) : AdminModelResponseDto {
        if(llModelRepository.existsById(request.id)){
            throw IllegalArgumentException("Model already exists")
        }
        val entity = request.toEntity()
        return AdminModelResponseDto(llModelRepository.save(entity))
    }

    @Transactional
    fun updateModel(id: String, request: AdminModelRequestDto) : AdminModelResponseDto {
        if(!llModelRepository.existsById(id)){
            throw IllegalArgumentException("Model not found")
        }
        val entity = request.toEntity() // ID 체크 및 기존 데이터 보존 로직 추가 가능
        return AdminModelResponseDto(llModelRepository.save(entity))
    }

    @Transactional
    fun deleteModel(id: String) {
        if(!llModelRepository.existsById(id)){
            throw IllegalArgumentException("Model not found")
        }
        llModelRepository.deleteById(id)
    }
}