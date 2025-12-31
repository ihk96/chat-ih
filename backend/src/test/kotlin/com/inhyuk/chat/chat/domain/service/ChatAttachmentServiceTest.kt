package com.inhyuk.chat.chat.domain.service

import com.inhyuk.chat.chat.domain.model.AttachmentContentType
import com.inhyuk.chat.chat.domain.model.ChatAttachmentEntity
import com.inhyuk.chat.chat.domain.repository.ChatAttachmentRepository
import com.inhyuk.chat.file.facade.FileFacade
import com.inhyuk.chat.file.facade.dto.FileDTO
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.data.message.TextContent
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import java.io.ByteArrayInputStream

class ChatAttachmentServiceTest : BehaviorSpec({
    val chatAttachmentRepository = mockk<ChatAttachmentRepository>()
    val fileFacade = mockk<FileFacade>()
    val chatAttachmentService = ChatAttachmentService(chatAttachmentRepository, fileFacade)

    Given("extractAndSave") {
        val fileDto = FileDTO(
            id = "file-1",
            fileName = "test.png",
            storedName = "stored-1",
            originalFileName = "test.png",
            storagePath = "path/to/test.png",
            mimeType = "image/png",
            size = 100L,
            userId = "user-1",
            isUsed = false,
            createdDate = java.time.LocalDateTime.now(),
            lastModifiedDate = null
        )

        When("이미지 파일인 경우") {
            val content = "fake image content".toByteArray()
            every { fileFacade.getFileStream(any()) } returns ByteArrayInputStream(content)
            every { chatAttachmentRepository.save(any()) } answers { firstArg() }

            val result = chatAttachmentService.extractAndSave(fileDto)

            Then("이미지 컨텐츠 타입으로 저장되어야 한다") {
                result.contentType shouldBe AttachmentContentType.IMAGE
                result.fileId shouldBe "file-1"
                verify { chatAttachmentRepository.save(any()) }
            }
        }
    }

    Given("convertAttachmentsToContents") {
        val attachmentIds = listOf("att-1", "att-2")
        val attachments = listOf(
            ChatAttachmentEntity(
                id = "att-1",
                fileId = "file-1",
                fileName = "img.png",
                contentType = AttachmentContentType.IMAGE,
                extractedText = "base64data",
                mimeType = "image/png"
            ),
            ChatAttachmentEntity(
                id = "att-2",
                fileId = "file-2",
                fileName = "doc.txt",
                contentType = AttachmentContentType.DOCUMENT,
                extractedText = "hello world",
                mimeType = "text/plain"
            )
        )

        When("첨부파일 ID 리스트를 전달하면") {
            every { chatAttachmentRepository.findAllById(attachmentIds) } returns attachments

            val results = chatAttachmentService.convertAttachmentsToContents(attachmentIds)

            Then("각 타입에 맞는 Content 객체 리스트를 반환해야 한다") {
                results.size shouldBe 2
                results[0].shouldBeInstanceOf<ImageContent>()
                results[1].shouldBeInstanceOf<TextContent>()
                (results[1] as TextContent).text() shouldBe """<file name="doc.txt" type="text/plain">hello world</file>"""
            }
        }
    }
})
