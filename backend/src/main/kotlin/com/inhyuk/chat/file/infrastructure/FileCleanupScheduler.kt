package com.inhyuk.chat.file.infrastructure

import com.inhyuk.chat.file.domain.FileService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FileCleanupScheduler(
    private val fileService: FileService
) {
    private val logger = LoggerFactory.getLogger(FileCleanupScheduler::class.java)

    /**
     * 매일 새벽 3시에 실행되어 24시간 동안 사용되지 않은 파일을 삭제합니다.
     */
    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupUnusedFiles() {
        logger.info("Starting cleanup of unused files...")
        val yesterday = LocalDateTime.now().minusDays(1)
        fileService.deleteUnusedFiles(yesterday)
        logger.info("Finished cleanup of unused files.")
    }
}
