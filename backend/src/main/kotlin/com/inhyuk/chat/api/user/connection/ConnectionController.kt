package com.inhyuk.chat.api.user.connection

import com.inhyuk.chat.api.user.connection.dto.ConnectionRequestDto
import com.inhyuk.chat.api.user.connection.dto.ConnectionResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/connections")
class ConnectionController(
    private val connectionUsecase: ConnectionUsecase
) {

    @GetMapping
    fun getConnections(): ResponseEntity<List<ConnectionResponseDto>> {
        return ResponseEntity.ok(connectionUsecase.getConnections())
    }

    @GetMapping("/{id}/models")
    fun getModels(@PathVariable id: String): ResponseEntity<List<String>> {
        return ResponseEntity.ok(connectionUsecase.getAvailableModels(id))
    }

    @PostMapping
    fun createConnection(@RequestBody request: ConnectionRequestDto): ResponseEntity<ConnectionResponseDto> {
        return ResponseEntity.ok(connectionUsecase.createConnection(request))
    }
}
