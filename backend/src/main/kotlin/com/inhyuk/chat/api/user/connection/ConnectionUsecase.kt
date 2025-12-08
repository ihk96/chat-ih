package com.inhyuk.chat.api.user.connection

import com.inhyuk.chat.api.user.connection.dto.ConnectionRequestDto
import com.inhyuk.chat.api.user.connection.dto.ConnectionResponseDto
import com.inhyuk.chat.domain.connection.ModelDiscoveryService
import com.inhyuk.chat.domain.connection.ModelProviderConnection
import com.inhyuk.chat.domain.connection.ModelProviderConnectionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ConnectionUsecase(
    private val connectionRepository: ModelProviderConnectionRepository,
    private val discoveryService: ModelDiscoveryService
) {

    fun getConnections(): List<ConnectionResponseDto> {
        return connectionRepository.findAll().map { ConnectionResponseDto(it) }
    }

    fun getAvailableModels(connectionId: String): List<String> {
        val connection = getConnection(connectionId)
        return discoveryService.getAvailableModels(connection)
    }

    @Transactional
    fun createConnection(request: ConnectionRequestDto): ConnectionResponseDto {
        // Validation logic can go here (e.g. check for duplicate names)
        val entity = request.toEntity()
        return ConnectionResponseDto(connectionRepository.save(entity))
    }

    fun getConnection(id: String): ModelProviderConnection {
        return connectionRepository.findById(id).orElseThrow { IllegalArgumentException("Connection not found") }
    }
}