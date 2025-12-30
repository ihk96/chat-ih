package com.inhyuk.chat.file.infrastructure

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.createTempDirectory

class LocalFileStorageProviderTest : BehaviorSpec({
    val tempDir = createTempDirectory("test-uploads").toString()
    val provider = LocalFileStorageProvider(tempDir)

    afterSpec {
        Paths.get(tempDir).toFile().deleteRecursively()
    }

    Given("store") {
        val content = "test content"
        val inputStream = ByteArrayInputStream(content.toByteArray())
        val storagePath = "2023/10/10/test.txt"

        When("파일을 저장하면") {
            val resultPath = provider.store(inputStream, storagePath)

            Then("지정된 경로에 파일이 생성되어야 한다") {
                val filePath = Paths.get(tempDir).resolve(storagePath)
                Files.exists(filePath) shouldBe true
                Files.readString(filePath) shouldBe content
                resultPath shouldBe storagePath
            }
        }
    }

    Given("delete") {
        val storagePath = "delete-test.txt"
        val filePath = Paths.get(tempDir).resolve(storagePath)
        
        Files.createDirectories(filePath.parent)
        Files.writeString(filePath, "to be deleted")

        When("파일을 삭제하면") {
            provider.delete(storagePath)

            Then("파일이 더 이상 존재하지 않아야 한다") {
                Files.exists(filePath) shouldBe false
            }
        }
    }
})
