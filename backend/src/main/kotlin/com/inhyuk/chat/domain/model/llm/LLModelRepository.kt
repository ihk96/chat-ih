package com.inhyuk.chat.domain.model.llm

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LLModelRepository : JpaRepository<LLModel, String> {

}