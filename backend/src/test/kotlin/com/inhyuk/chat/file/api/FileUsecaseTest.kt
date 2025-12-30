package com.inhyuk.chat.file.api

import com.inhyuk.chat.file.domain.FileEntity
import com.inhyuk.chat.file.domain.FileService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.InputStream

class FileUsecaseTest : BehaviorSpec({
    val fileService = mockk<FileService>()
    val fileUsecase = FileUsecase(fileService)

    Given("uploadFile") {
        val inputStream = mockk<InputStream>()
        val originalFileName = "test.txt"
        val contentType = "text/plain"
        val size = 100L
        val userId = "user-123"

        val fileEntity = FileEntity(
            id = "file-id-123",
            originalFileName = originalFileName,
            storagePath = "2023/12/30/uuid.txt",
            contentType = contentType,
            size = size,
            userId = userId
        )

        When("파일 업로드 요청이 오면") {
            every {
                fileService.uploadFile(any(), any(), any(), any(), any())
            } returns fileEntity

            val result = fileUsecase.uploadFile(
                inputStream = inputStream,
                originalFileName = originalFileName,
                contentType = contentType,
                size = size,
                userId = userId
            )

            Then("서비스를 호출하고 결과를 DTO로 변환하여 반환해야 한다") {
                result.id shouldBe fileEntity.id
                result.fileName shouldBe fileEntity.originalFileName
                result.size shouldBe fileEntity.size
                result.contentType shouldBe fileEntity.contentType

                verify {
                    fileService.uploadFile(
                        inputStream = inputStream,
                        originalFileName = originalFileName,
                        contentType = contentType,
                        size = size,
                        userId = userId
                    )
                }
            }
        }
    }

    Given("getFile") {
        val fileId = "file-123"
        val userId = "user-123"
        val fileEntity = FileEntity(
            id = fileId,
            originalFileName = "test.txt",
            storagePath = "path/to/file",
            contentType = "text/plain",
            size = 100L,
            userId = userId
        )

        When("본인이 자신의 파일 정보를 조회하면") {
            every { fileService.findById(fileId) } returns fileEntity

            val result = fileUsecase.getFile(fileId, userId)

            Then("파일 정보를 DTO로 변환하여 반환해야 한다") {
                result.id shouldBe fileId
                result.userId shouldBe userId
            }
        }

        When("다른 사용자가 파일 정보를 조회하면") {
            every { fileService.findById(fileId) } returns fileEntity

            Then("IllegalArgumentException이 발생해야 한다") {
                io.kotest.assertions.throwables.shouldThrow<IllegalArgumentException> {
                    fileUsecase.getFile(fileId, "other-user")
                }
            }
        }
    }

    Given("downloadFile") {
        val fileId = "file-123"
        val userId = "user-123"
        val fileEntity = FileEntity(
            id = fileId,
            originalFileName = "test.txt",
            storagePath = "path/to/file",
            contentType = "text/plain",
            size = 100L,
            userId = userId
        )
        val inputStream = mockk<InputStream>()

        When("본인이 자신의 파일을 다운로드하면") {
            every { fileService.findById(fileId) } returns fileEntity
            every { fileService.getFileStream(fileEntity.storagePath) } returns inputStream

            val result = fileUsecase.downloadFile(fileId, userId)

            Then("다운로드 정보를 DTO로 반환해야 한다") {
                result.fileName shouldBe fileEntity.originalFileName
                result.inputStream shouldBe inputStream
            }
        }

        When("다른 사용자가 파일을 다운로드하려고 하면") {
            every { fileService.findById(fileId) } returns fileEntity

            Then("IllegalArgumentException이 발생해야 한다") {
                io.kotest.assertions.throwables.shouldThrow<IllegalArgumentException> {
                    fileUsecase.downloadFile(fileId, "other-user")
                }
            }
        }
    }
})
