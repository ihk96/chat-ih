package com.inhyuk.chat.chat.api.dto

import com.inhyuk.chat.chat.domain.model.AttachmentContentType
import com.inhyuk.chat.chat.domain.model.ChatAttachmentEntity
import jakarta.validation.constraints.NotBlank

data class ChatRequestDto(
    @field:NotBlank(message = "Message cannot be blank")
    val message : String,

    @field:NotBlank(message = "Model cannot be blank")
    val model : String,

    val attachments : List<String>? = null
)

data class ChatAttachmentResponseDto(
    val id: String?,
    val fileName: String,
    val contentType: AttachmentContentType,
){
    constructor(attachment: ChatAttachmentEntity) : this(
        id = attachment.fileId,
        fileName = attachment.fileName,
        contentType = attachment.contentType
    )
}