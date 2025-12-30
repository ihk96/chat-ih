package com.inhyuk.chat.file.infrastructure

import com.inhyuk.chat.file.domain.FileService
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify

class FileCleanupSchedulerTest : BehaviorSpec({
    val fileService = mockk<FileService>(relaxed = true)
    val scheduler = FileCleanupScheduler(fileService)

    Given("cleanupUnusedFiles") {
        When("스케줄러가 실행되면") {
            scheduler.cleanupUnusedFiles()

            Then("fileService의 deleteUnusedFiles가 호출되어야 한다") {
                verify { fileService.deleteUnusedFiles(any()) }
            }
        }
    }
})
