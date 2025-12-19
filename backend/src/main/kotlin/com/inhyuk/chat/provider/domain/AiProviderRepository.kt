package com.inhyuk.chat.provider.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AiProviderRepository : JpaRepository<AiProviderEntity, String>