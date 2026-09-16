package com.rafambn.profilebanner.logging

import com.rafambn.scribe.Archivist
import com.rafambn.scribe.Entry
import com.rafambn.scribe.slf4j.Slf4jScribe
import com.rafambn.scribe.slf4j.ScribeBackend
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.serialization.json.JsonObject
import org.slf4j.Marker
import org.slf4j.event.Level
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.WRITE

@ScribeBackend
object AppScribe : Slf4jScribe() {
    private lateinit var logFilePath: Path

    override val bufferCapacity = 1_024
    override val bufferOverflow = BufferOverflow.DROP_OLDEST
    override val archivists = listOf(
        Archivist { entry ->
            val json = JsonObject(entry).toString()
            println(json)
            appendToFile(json)
        }
    )
    override val onArchiveFailure: (Archivist, Entry, Throwable) -> Unit =
        { _, _, error ->
            System.err.println("Scribe archivist failed: ${error.stackTraceToString()}")
        }

    override fun isEnabled(
        loggerName: String,
        level: Level,
        marker: Marker?
    ): Boolean = level.toInt() >= Level.INFO.toInt()

    fun configure(logFilePath: Path) {
        this.logFilePath = logFilePath
        logFilePath.toAbsolutePath().parent?.let(Files::createDirectories)
    }

    @Synchronized
    private fun appendToFile(json: String) {
        check(::logFilePath.isInitialized) { "AppScribe must be configured before hire" }
        Files.newBufferedWriter(
            logFilePath,
            StandardCharsets.UTF_8,
            CREATE,
            WRITE,
            APPEND
        ).use { writer ->
            writer.appendLine(json)
        }
    }
}
