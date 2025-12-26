package com.inhyuk.chat.prompt.domain

import com.inhyuk.chat.model.facade.LLModelFacade
import com.inhyuk.chat.user.facade.UserFacade
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PromptVariablesService(
    private val userFacade: UserFacade,
    private val lLModelFacade: LLModelFacade
) {
    fun getVariableKeys(userId: String?): List<String> {
        return listOf("datetime", "username","model_name")
    }

    fun getVariables(userId: String?, modelId: String?): Map<String, String> {
        val variables = mutableMapOf<String, String>()

        variables["datetime"] = LocalDateTime.now().toString()

        userId?.let {
            userFacade.getUserById(it)?.let { user -> variables["username"] = user.username }

        }

        modelId?.let {
            lLModelFacade.getModelById(it)?.let { model -> variables["model_name"] = model.publicName }
        }

        return variables
    }
}