package com.inhyuk.chat.api.user.model

import com.inhyuk.chat.domain.model.llm.LLModel
import com.inhyuk.chat.domain.model.llm.LLModelRepository
import com.inhyuk.chat.domain.model.llm.LLModelService
import org.springframework.stereotype.Service

@Service
class BasicLLModelUsecase(
    private val llModelRepository: LLModelRepository,
    private val llMmodelService: LLModelService
){

    fun getModels() : List<LLModel> {
        return llModelRepository.findAll()
    }
}