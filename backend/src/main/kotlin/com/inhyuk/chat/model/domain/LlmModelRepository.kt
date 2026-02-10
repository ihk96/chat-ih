package com.inhyuk.chat.model.domain

import org.springframework.data.jpa.repository.JpaRepository

interface LlmModelRepository : JpaRepository<LlmModelEntity, String>
