package com.inhyuk.chat.domain.connection

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ModelProviderConnectionRepository : JpaRepository<ModelProviderConnection, String>
