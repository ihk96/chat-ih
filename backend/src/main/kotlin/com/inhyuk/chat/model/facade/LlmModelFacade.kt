package com.inhyuk.chat.model.facade

import com.inhyuk.chat.model.domain.LlmModelRepository
import com.inhyuk.chat.model.domain.ModelStatus
import org.springframework.stereotype.Service

@Service
class LlmModelFacade(
    private val modelRepository: LlmModelRepository
) {
    fun get(id: String): LlmModelInfo {
        val model = modelRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Model not found") }
        return LlmModelInfo(
            id = model.id,
            providerId = model.providerId,
            originName = model.originName
        )
    }

    fun getActive(id: String): LlmModelInfo {
        val model = modelRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Model not found") }
        if (model.status != ModelStatus.ACTIVE) {
            throw IllegalArgumentException("Model is inactive")
        }
        return LlmModelInfo(
            id = model.id,
            providerId = model.providerId,
            originName = model.originName
        )
    }
}
