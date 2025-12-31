package com.inhyuk.chat.chat.api

import com.inhyuk.chat.chat.api.dto.ChatAttachmentResponseDto
import com.inhyuk.chat.chat.api.dto.ChatMessageDto
import com.inhyuk.chat.chat.api.dto.ChatSessionDto
import com.inhyuk.chat.chat.domain.service.ChatAttachmentService
import com.inhyuk.chat.chat.domain.service.ChatService
import com.inhyuk.chat.chat.domain.ChatSessionProvider
import com.inhyuk.chat.chat.domain.model.ChatAttachmentEntity
import com.inhyuk.chat.chat.domain.model.ChatMessageEntity
import com.inhyuk.chat.chat.domain.repository.ChatAttachmentRepository
import com.inhyuk.chat.chat.domain.repository.ChatMessageRepository
import com.inhyuk.chat.chat.domain.repository.ChatSessionRepository
import com.inhyuk.chat.chat.domain.service.SummaryService
import com.inhyuk.chat.file.facade.FileFacade
import com.inhyuk.chat.model.facade.LLModelFacade
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessageDeserializer
import dev.langchain4j.data.message.ChatMessageType
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.StreamingChatModel
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class BasicChatUsecase(
    private val chatService: ChatService,
    private val llModelFacade: LLModelFacade,
    private val sessionProvider: ChatSessionProvider,
    private val summaryService: SummaryService,
    private val fileFacade: FileFacade,
    private val chatAttachmentService: ChatAttachmentService,
    private val chatSessionRepository: ChatSessionRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatAttachmentRepository: ChatAttachmentRepository
) {

    private val streamPipes = mutableMapOf<String, ChatStreamPipe>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @PreDestroy
    fun cleanup() {
        coroutineScope.cancel()
    }

    @Transactional
    fun initSession(userId: String, message: String, modelId: String, attachments: List<String>?): String {
        val model = llModelFacade.getStreamChatModel(modelId)
        val session = chatService.addNewChatSession(userId)

        coroutineScope.launch {
            chatSse(session.id, message, model, attachments)
        }

        // Auto-summary trigger
        summaryService.generateSummary(session.id, message, modelId)

        return session.id
    }


    @Transactional
    fun chat(userId: String, sessionId: String, message: String, modelId: String, attachments : List<String>?) : SseEmitter {
        val model = llModelFacade.getStreamChatModel(modelId)
        val session = sessionProvider.getSession(sessionId) ?:run { throw IllegalArgumentException("Session Not Found") }
        val entity = session.sessionEntity
        if(entity.userId != userId) {
            throw IllegalArgumentException("Session User Id Not Match")
        }
        if(session.isActive){
            throw IllegalArgumentException("Session Already Active")
        }

        return chatSse(sessionId, message, model, attachments)
    }

    fun chatSse(sessionId: String, message: String, model: StreamingChatModel, attachments : List<String>?) : SseEmitter {
        val session = sessionProvider.getSession(sessionId) ?:run { throw IllegalArgumentException("Session Not Found") }

        val emitter = SseEmitter()
        val tokenStream = chatService.chatStream(session, message, model, attachments)
        val pipe = ChatStreamPipe(sessionId, tokenStream)
        streamPipes[sessionId] = pipe
        pipe.subscribe(emitter)
        tokenStream.start()

        return emitter
    }

    fun subscribe(userId: String, sessionId: String) : SseEmitter {
        val session = sessionProvider.getSession(sessionId) ?:run { throw IllegalArgumentException("Session Not Found") }
        if(userId != session.userId){
            throw IllegalArgumentException("Session User Id Not Match")
        }
        if(!session.isActive){
            throw IllegalArgumentException("Session Not Active")
        }

        val emitter = SseEmitter()
        streamPipes[sessionId]?.subscribe(emitter) ?:emitter.completeWithError(IllegalStateException("Session Not Active"))

        return emitter
    }

    fun getSession(userId: String, sessionId: String) : ChatSessionDto {
        val session = chatSessionRepository.findById(sessionId).orElseThrow { IllegalArgumentException("Session Not Found") }
        if(userId != session.userId){
            throw IllegalArgumentException("Session User Id Not Match")
        }
        return ChatSessionDto(session)
    }

    fun getSessionMessageHistory(userId: String, sessionId : String) : List<ChatMessageDto>{
        val session = chatSessionRepository.findById(sessionId).orElseThrow { IllegalArgumentException("Session Not Found") }
        if(userId != session.userId){
            throw IllegalArgumentException("Session User Id Not Match")
        }
        val messages = chatMessageRepository.findByChatSessionIdOrderByCreatedDateAsc(sessionId)

        val needAttachment = messages.filter { it.attachments.isNotEmpty() }
        val attachmentIds = needAttachment.flatMap { it.attachments }
        val attachments = chatAttachmentRepository.findAllById(attachmentIds)
        val attachmentMap = attachments.associateBy { it.id!! }

        return messages.map { message ->
            val chatMessage = ChatMessageDeserializer.messageFromJson(message.message)
            when (chatMessage.type()) {
                ChatMessageType.USER -> {
                    val userMessage = chatMessage as UserMessage
                    ChatMessageDto(
                        text = userMessage.singleText(),
                        type = chatMessage.type(),
                        attachments = if (message.attachments.isNotEmpty()) message.attachments.map {
                            attachmentMap[it]?.let { attachment ->
                                ChatAttachmentResponseDto(
                                    attachment
                                )
                            }
                        }.filterNotNull() else emptyList(),
                        thinking = "",
                        toolRequests = emptyList()

                    )
                }

                ChatMessageType.AI -> {
                    val aiMessage = chatMessage as AiMessage
                    ChatMessageDto(
                        text = aiMessage.text(),
                        type = chatMessage.type(),
                        attachments = emptyList(),
                        thinking = aiMessage.thinking(),
                        toolRequests = aiMessage.toolExecutionRequests().map { it.name() }

                    )
                }

                else -> null
            }
        }.filterNotNull()
    }

    fun getSessions(userId: String, pageable: Pageable): Page<ChatSessionDto> {
        return chatSessionRepository.findAllByUserId(userId, pageable).map { ChatSessionDto(it) }
    }

    @Transactional
    fun deleteSession(userId: String, sessionId: String) {
        val session = sessionProvider.getSession(sessionId) ?: throw IllegalArgumentException("Session Not Found")
        if (session.userId != userId) {
            throw IllegalArgumentException("Session User Id Not Match")
        }
        sessionProvider.deleteSession(sessionId)
        streamPipes.remove(sessionId)?.cleanup()
    }

    @Transactional
    fun updateSessionTitle(userId: String, sessionId: String, title: String) {
        val session = sessionProvider.getSession(sessionId) ?: throw IllegalArgumentException("Session Not Found")
        if (session.userId != userId) {
           throw IllegalArgumentException("Session User Id Not Match")
        }
        session.sessionEntity.title = title
        sessionProvider.saveSession(session.sessionEntity)
    }

    @Transactional
    fun uploadAttachment(userId: String, file : MultipartFile): ChatAttachmentResponseDto {
        val file = fileFacade.uploadFile(file.inputStream, file.originalFilename!!, file.contentType!!, file.size, userId)
        val attachment = chatAttachmentService.extractAndSave(file)
        return ChatAttachmentResponseDto(attachment)
    }
}