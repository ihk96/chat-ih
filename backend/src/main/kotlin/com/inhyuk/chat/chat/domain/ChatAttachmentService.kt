package com.inhyuk.chat.chat.domain

import com.inhyuk.chat.chat.domain.model.AttachmentContentType
import com.inhyuk.chat.file.facade.FileFacade
import com.inhyuk.chat.file.facade.dto.FileDTO
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.data.message.PdfFileContent
import dev.langchain4j.data.message.TextContent
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class ChatAttachmentService(
    private val chatAttachmentRepository: ChatAttachmentRepository,
    private val fileFacade: FileFacade
) {
    private val pdfParser: ApachePdfBoxDocumentParser = ApachePdfBoxDocumentParser()
    private val tikaParser: ApacheTikaDocumentParser = ApacheTikaDocumentParser()

    fun loadDocument(file: FileDTO){
        val fileType = determineFileType(file.mimeType)
        when(fileType){
            AttachmentContentType.IMAGE -> {
                val inputStream = fileFacade.getFileStream(file.storagePath)
                val base64 = Base64.getEncoder().encodeToString(inputStream.readBytes())
                ImageContent.from(base64, file.mimeType)
            }
            AttachmentContentType.PDF -> {
                val document = FileSystemDocumentLoader.loadDocument(file.storagePath, pdfParser)
                TextContent.from(document.text())
            }
            AttachmentContentType.DOCUMENT -> {
                val document = FileSystemDocumentLoader.loadDocument(file.storagePath, tikaParser)
                TextContent.from(document.text())
            }
            AttachmentContentType.OTHER -> null
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