package com.inhyuk.chat.chat.domain.service

import com.inhyuk.chat.chat.domain.repository.ChatAttachmentRepository
import com.inhyuk.chat.chat.domain.model.AttachmentContentType
import com.inhyuk.chat.chat.domain.model.ChatAttachmentEntity
import com.inhyuk.chat.file.facade.FileFacade
import com.inhyuk.chat.file.facade.dto.FileDTO
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser
import dev.langchain4j.data.message.Content
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.data.message.TextContent
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class ChatAttachmentService(
    private val chatAttachmentRepository: ChatAttachmentRepository,
    private val fileFacade: FileFacade
) {
    private val tikaParser: ApacheTikaDocumentParser = ApacheTikaDocumentParser()


    @Transactional
    fun extractAndSave(file: FileDTO): ChatAttachmentEntity {
        val extractText = extract(file) ?: throw IllegalStateException("Failed to extract text from file: ${file.id}")
        return chatAttachmentRepository.save(ChatAttachmentEntity(
            fileId = file.id!!,
            fileName = file.originalFileName,
            contentType = determineFileType(file.mimeType),
            extractedText = extractText,
            mimeType = file.mimeType
        ))
    }

    @Transactional
    fun convertAttachmentsToContents(attachmentIds: List<String>): List<Content> {
        val attachments = chatAttachmentRepository.findAllById(attachmentIds)

        val contents = attachments.filter { it.contentType != AttachmentContentType.OTHER }.mapNotNull {
            when (it.contentType) {
                AttachmentContentType.IMAGE -> ImageContent(it.extractedText, it.mimeType)
                AttachmentContentType.PDF, AttachmentContentType.DOCUMENT -> TextContent("""<file name="${it.fileName}" type="${it.mimeType}">${it.extractedText}</file>""".trimIndent())
                AttachmentContentType.OTHER -> null
            }
        }
        attachments.forEach {
            it.isUsed = true
            fileFacade.useFile(it.fileId)
        }
        return contents
    }

    fun extract(file: FileDTO): String?{
        val fileType = determineFileType(file.mimeType)
        when(fileType){
            AttachmentContentType.IMAGE -> {
                val inputStream = fileFacade.getFileStream(file.storagePath)
                val base64 = Base64.getEncoder().encodeToString(inputStream.readBytes())
                return base64
            }
            AttachmentContentType.PDF -> {
                val document = FileSystemDocumentLoader.loadDocument(file.storagePath, tikaParser)
                return document.text()
            }
            AttachmentContentType.DOCUMENT -> {
                val document = FileSystemDocumentLoader.loadDocument(file.storagePath, tikaParser)
                return document.text()
            }
            AttachmentContentType.OTHER -> return null
        }
    }

    private fun determineFileType(mimeType: String): AttachmentContentType {
        return when {
            mimeType.startsWith("image/") -> AttachmentContentType.IMAGE
            mimeType == "application/pdf" -> AttachmentContentType.PDF
            mimeType in DOCUMENT_MIME_TYPES -> AttachmentContentType.DOCUMENT
            else -> AttachmentContentType.OTHER
        }
    }

    companion object {
        private val DOCUMENT_MIME_TYPES = setOf(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "text/markdown"
        )
    }
}