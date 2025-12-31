package com.inhyuk.chat.file.api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.inhyuk.chat.file.api.dto.FileDownloadDto
import com.inhyuk.chat.file.api.dto.FileResponseDto
import com.inhyuk.chat.file.api.dto.FileUploadResponseDto
import com.inhyuk.chat.common.controller.RestResponseAdvice
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.core.Authentication
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.io.ByteArrayInputStream
import java.io.InputStream

class FileControllerTest : BehaviorSpec({
    val fileUsecase = mockk<FileUsecase>()
    val controller = FileController(fileUsecase)
    val objectMapper = jacksonObjectMapper()
    val mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(RestResponseAdvice(objectMapper))
        .build()

    Given("uploadFile") {
        val fileName = "test.txt"
        val content = "hello world".toByteArray()
        val contentType = "text/plain"
        val userId = "user-1"
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns userId
        every { authentication.name } returns userId

        When("정상적인 파일 업로드 요청이 오면") {
            val file = MockMultipartFile("file", fileName, contentType, content)
            val responseDto = FileUploadResponseDto(
                id = "file-id",
                fileName = fileName,
                size = content.size.toLong(),
                mimeType = contentType
            )

            every {
                fileUsecase.uploadFile(
                    inputStream = any(InputStream::class),
                    originalFileName = fileName,
                    contentType = contentType,
                    size = content.size.toLong(),
                    userId = userId
                )
            } returns responseDto

            val result = mockMvc.perform(
                multipart("/api/v1/files/upload")
                    .file(file)
                    .principal(authentication)
            )

            Then("200 OK와 파일 정보를 반환한다") {
                result.andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.id").value("file-id"))
                    .andExpect(jsonPath("$.data.fileName").value(fileName))
                
                verify {
                    fileUsecase.uploadFile(any(), fileName, contentType, any(), userId)
                }
            }
        }

        When("빈 파일 업로드 요청이 오면") {
            val emptyFile = MockMultipartFile("file", fileName, contentType, ByteArray(0))

            val result = mockMvc.perform(
                multipart("/api/v1/files/upload")
                    .file(emptyFile)
                    .principal(authentication)
            )

            Then("400 Bad Request를 반환한다") {
                result.andExpect(status().isBadRequest)
            }
        }

        When("인증 정보가 없으면") {
            val file = MockMultipartFile("file", fileName, contentType, content)
            val authWithoutPrincipal = mockk<Authentication>()
            every { authWithoutPrincipal.principal } returns null
            every { authWithoutPrincipal.name } returns null

            val result = mockMvc.perform(
                multipart("/api/v1/files/upload")
                    .file(file)
                    .principal(authWithoutPrincipal)
            )

            Then("401 Unauthorized를 반환한다") {
                result.andExpect(status().isUnauthorized)
            }
        }
    }

    Given("getFile") {
        val fileId = "file-123"
        val userId = "user-1"
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns userId
        every { authentication.name } returns userId

        When("파일 정보 조회 요청이 오면") {
            val responseDto = FileResponseDto(
                id = fileId,
                fileName = "test.txt",
                size = 100L,
                mimeType = "text/plain",
                userId = userId,
                isUsed = false
            )

            every { fileUsecase.getFile(fileId, userId) } returns responseDto

            val result = mockMvc.perform(
                get("/api/v1/files/$fileId")
                    .principal(authentication)
            )

            Then("200 OK와 파일 상세 정보를 반환한다") {
                result.andExpect(status().isOk)
                    .andExpect(jsonPath("$.data.id").value(fileId))
                    .andExpect(jsonPath("$.data.userId").value(userId))
            }
        }
    }

    Given("downloadFile") {
        val fileId = "file-123"
        val userId = "user-1"
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns userId
        every { authentication.name } returns userId

        When("파일 다운로드 요청이 오면") {
            val content = "file content".toByteArray()
            val downloadDto = FileDownloadDto(
                fileName = "test.txt",
                mimeType = "text/plain",
                inputStream = ByteArrayInputStream(content)
            )

            every { fileUsecase.downloadFile(fileId, userId) } returns downloadDto

            val result = mockMvc.perform(
                get("/api/v1/files/$fileId/download")
                    .principal(authentication)
            )

            Then("200 OK와 파일 스트림을 반환한다") {
                result.andExpect(status().isOk)
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.txt\""))
                    .andExpect(content().bytes(content))
            }
        }
    }
})
