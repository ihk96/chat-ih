package com.inhyuk.chat.domain.chat

import com.inhyuk.chat.usecase.dto.StreamEventDto
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

class ChatSessionTokenStream(
    val sessionId: String,
) {
    val messages : MutableList<TokenStreamMessage> = mutableListOf()
    var currentType : String = ""
    var currentMessage : String = ""
    private val emitters : MutableList<SseEmitter> = mutableListOf()

    fun append(type: String, chunk : String){
        if(currentType == ""){ // 초기 토큰이라면
            currentType = type
            currentMessage = chunk
        } else if(type == currentType){ // 타입이 같은 토큰이라면
            currentMessage += chunk
            return
        } else { // 타입이 다른 토큰이라면
            messages.add(TokenStreamMessage(type = currentType, message = currentMessage))
            currentType = type
            currentMessage = chunk
        }
        emit(type, chunk)
    }

    private fun emit(type: String,message: String){
        emitters.removeIf { emitter ->
            try {
                val event = StreamEventDto(type, message)
                emitter.send(SseEmitter.event().data(event))
                false
            } catch (e: IOException) {
                emitter.completeWithError(e)
                true
            }
         }
    }

    fun subscribe(emitter: SseEmitter){
        emitters.add(emitter);
    }

    fun complete(){
        emitters.forEach { it.complete() }
        emitters.clear()
    }

}


class TokenStreamMessage(
    val type : String,
    val message : String,
)