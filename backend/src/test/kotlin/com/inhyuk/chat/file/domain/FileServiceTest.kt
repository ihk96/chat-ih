package com.inhyuk.chat.file.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import java.io.InputStream
import java.time.LocalDateTime
import java.util.*

class FileServiceTest : BehaviorSpec({
    val fileRepository = mockk<FileRepository>()
    val fileStorageProvider = mockk<FileStorageProvider>()
    val fileService = FileService(fileRepository, fileStorageProvider)

    Given("uploadFile") {
        val inputStream = mockk<InputStream>()
        val originalFileName = "test.txt"
        val contentType = "text/plain"
        val size = 100L
        val userId = "user-123"

        When("파일을 업로드하면") {
            every { fileStorageProvider.store(any(), any()) } returns "path/to/file"
            every { fileRepository.save(any()) } answers { firstArg() }

            val result = fileService.uploadFile(inputStream, originalFileName, contentType, size, userId)

            Then("파일 엔티티가 저장되고 반환되어야 한다") {
                result.originalFileName shouldBe originalFileName
                result.mimeType shouldBe contentType
                result.size shouldBe size
                result.userId shouldBe userId
                result.isUsed shouldBe false
                verify { fileStorageProvider.store(any(), any()) }
                verify { fileRepository.save(any()) }
            }
        }
    }
    
    Given("getFileStream") {
        val storagePath = "path/to/file"
        val inputStream = mockk<InputStream>()

        When("저장 경로로 파일 스트림을 요청하면") {
            every { fileStorageProvider.load(storagePath) } returns inputStream

            val result = fileService.getFileStream(storagePath)

            Then("스토리지 프로바이더로부터 스트림을 반환받아야 한다") {
                result shouldBe inputStream
                verify { fileStorageProvider.load(storagePath) }
            }
        }
    }

    Given("markAsUsed") {
        val fileId = UUID.randomUUID().toString()
        val fileEntity = FileEntity(
            id = fileId,
            originalFileName = "test.txt",
            storedName = "stored-name",
            storagePath = "path/to/file",
            mimeType = "text/plain",
            size = 100L,
            userId = "user-123",
            isUsed = false
        )

        When("파일을 사용됨으로 표시하면") {
            every { fileRepository.findById(fileId) } returns Optional.of(fileEntity)

            fileService.markAsUsed(fileId)

            Then("isUsed 플래그가 true가 되어야 한다") {
                fileEntity.isUsed shouldBe true
            }
        }
    }

    Given("deleteUnusedFiles") {
        val before = LocalDateTime.now()
        val unusedFile = FileEntity(
            id = "unused-1",
            originalFileName = "unused.txt",
            storedName = "stored-unused",
            storagePath = "path/unused",
            mimeType = "text/plain",
            size = 50L,
            userId = "user-1",
            isUsed = false
        )

        When("미사용 파일을 삭제하면") {
            every { fileRepository.findAllByIsUsedFalseAndCreatedDateBefore(before) } returns listOf(unusedFile)
            every { fileStorageProvider.delete(any()) } just Runs
            every { fileRepository.delete(any()) } just Runs

            fileService.deleteUnusedFiles(before)

            Then("스토리지와 DB에서 파일이 삭제되어야 한다") {
                verify { fileStorageProvider.delete(unusedFile.storagePath) }
                verify { fileRepository.delete(unusedFile) }
            }
        }
    }
})
