package com.rafambn.profilebanner.github

import com.rafambn.profilebanner.pinnedRepos
import com.rafambn.profilebanner.logging.AppScribe
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class GitHubStarsTest {
    @Test
    fun cachesCountsPreservesGoodValuesAndWaitsOutRateLimits() = runBlocking {
        val logPath = Path.of("build/test-logs/profile.log")
        Files.createDirectories(logPath.parent)
        AppScribe.configure(logPath)
        var now = Instant.parse("2026-10-02T12:00:00Z")
        val clock = object : Clock() {
            override fun instant(): Instant = now
            override fun getZone(): ZoneId = ZoneOffset.UTC
            override fun withZone(zone: ZoneId): Clock = this
        }
        val phase = AtomicInteger()
        val requests = AtomicInteger()
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/repos/rafambn/") { exchange ->
            requests.incrementAndGet()
            val repository = exchange.requestURI.path.substringAfterLast('/')
            val status = when {
                phase.get() == 2 -> 429
                phase.get() == 1 && repository == "FrameBar" -> 500
                else -> 200
            }
            val body = when {
                phase.get() == 1 && repository == "KMaP" -> "{invalid"
                repository == "FrameBar" -> """{"stargazers_count":0}"""
                else -> """{"stargazers_count":${if (phase.get() == 1) 42 else 10}}"""
            }.toByteArray()
            if (status == 429) {
                exchange.responseHeaders.add("Retry-After", "3600")
                exchange.responseHeaders.add("X-RateLimit-Reset", now.plusSeconds(7200).epochSecond.toString())
            }
            exchange.sendResponseHeaders(status, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }
        server.start()
        try {
            GitHubStars("http://127.0.0.1:${server.address.port}", clock).use { stars ->
                stars.refresh()
                assertEquals(pinnedRepos.size, stars.counts.size)
                assertEquals(0L, stars.counts["FrameBar"])
                stars.refresh()
                assertEquals(6, requests.get(), "Repeated reads must not spend the API quota")

                now = now.plusSeconds(16 * 60)
                phase.set(1)
                stars.refresh()
                assertEquals(10L, stars.counts["KMaP"], "Malformed JSON must preserve the previous value")
                assertEquals(0L, stars.counts["FrameBar"], "HTTP failures must preserve a real zero")
                assertEquals(42L, stars.counts["KFlate"])

                now = now.plusSeconds(16 * 60)
                phase.set(2)
                stars.refresh()
                assertEquals(13, requests.get(), "A rate limit must stop the remaining repository requests")
                now = now.plusSeconds(90 * 60)
                phase.set(0)
                stars.refresh()
                assertEquals(13, requests.get(), "The later rate-limit reset must win over Retry-After")
                now = now.plusSeconds(31 * 60)
                stars.refresh()
                assertEquals(19, requests.get())
                assertEquals(10L, stars.counts["KFlate"])
            }
        } finally {
            server.stop(0)
        }
    }
}
