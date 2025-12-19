package com.inhyuk.chat.model.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LLModelRepository : JpaRepository<LLModelEntity, String>