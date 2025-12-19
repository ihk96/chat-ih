package com.inhyuk.chat.chat.api

import com.inhyuk.chat.chat.api.dto.StreamEventDto
import com.inhyuk.chat.chat.domain.model.ActiveTokenStream
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class ChatStreamPipe(
    val sessionId : String,
    val activeTokenStream: ActiveTokenStream
) {
    // SSE Emitters (thread-safe)
    private val emitters = ConcurrentHashMap.newKeySet<SseEmitter>()



    /**
     * SSE 이벤트 전송
     */
    private fun emit(type: String, message: String) {
        val event = StreamEventDto(type, message)
        val failedEmitters = mutableSetOf<SseEmitter>()

        emitters.forEach { emitter ->
            try {
                emitter.send(SseEmitter.event().data(event))
            } catch (e: IOException) {
                emitter.completeWithError(e)
                failedEmitters.add(emitter)
            }
        }

        // 실패한 emitter 제거
        failedEmitters.forEach { emitters.remove(it) }
    }

    private fun complete(){
        emitters.forEach {
            try {
                it.complete()
            } catch (e: Exception) {
            }
        }
        emitters.clear()
    }

    /**
     * 스트림 에러 처리
     */
    fun error(error: Throwable) {
        emitters.forEach {
            try {
                it.completeWithError(error)
            } catch (e: Exception) {
            }
        }
        emitters.clear()
    }

    /**
     * 스트림 강제 정리 (cleanup)
     */
    fun cleanup() {
        emitters.forEach {
            try {
                it.complete()
            } catch (e: Exception) {
                // 이미 완료된 경우 무시
            }
        }
        emitters.clear()
    }

    /**
     * SSE Emitter 구독
     */
    fun subscribe(emitter: SseEmitter) {
        emitters.add(emitter)
        emitter.send(SseEmitter.event().data(StreamEventDto(activeTokenStream.currentType, activeTokenStream.currentMessage)))
    }


}