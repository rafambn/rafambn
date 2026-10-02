package com.rafambn.profilebanner.github

import com.rafambn.profilebanner.pinnedRepos
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Clock
import java.time.Duration
import java.time.Instant

class GitHubStars(
    private val apiBaseUrl: String = "https://api.github.com",
    private val clock: Clock = Clock.systemUTC()
) : AutoCloseable {
    private val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()
    private val log = LoggerFactory.getLogger(GitHubStars::class.java)
    private var nextRefresh = Instant.MIN
    private var rateLimitDelay = Duration.ofMinutes(15)

    @Volatile
    var counts: Map<String, Long> = emptyMap()
        private set

    // One application coroutine owns refreshes; request handlers only read immutable snapshots.
    suspend fun refresh() {
        val now = clock.instant()
        if (now < nextRefresh) return
        nextRefresh = now.plus(Duration.ofMinutes(15))

        for (repository in pinnedRepos) {
            try {
                val request = HttpRequest.newBuilder(URI.create("$apiBaseUrl/repos/rafambn/$repository"))
                    .timeout(Duration.ofSeconds(8))
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .header("User-Agent", "rafambn-profile")
                    .GET().build()
                val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()
                if (response.statusCode() == 403 || response.statusCode() == 429) {
                    val retryAfter = response.headers().firstValue("retry-after").orElse("").toLongOrNull()
                    val reset = response.headers().firstValue("x-ratelimit-reset").orElse("").toLongOrNull()
                    val receivedAt = clock.instant()
                    nextRefresh = maxOf(
                        nextRefresh,
                        receivedAt.plus(rateLimitDelay),
                        receivedAt.plusSeconds(retryAfter ?: 0),
                        reset?.let(Instant::ofEpochSecond) ?: receivedAt
                    )
                    rateLimitDelay = rateLimitDelay.multipliedBy(2).coerceAtMost(Duration.ofHours(24))
                    log.warn("GitHub stars rate limited; keeping cached counts until {}", nextRefresh)
                    return
                }
                check(response.statusCode() == 200) { "GitHub returned HTTP ${response.statusCode()}" }
                val body = Json.parseToJsonElement(response.body()) as? JsonObject
                val stars = (body?.get("stargazers_count") as? JsonPrimitive)?.longOrNull
                check(stars != null && stars >= 0) { "GitHub returned an invalid star count" }
                counts = counts + (repository to stars)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                log.warn("Could not refresh GitHub stars for {}: {}", repository, error.message)
            }
        }
        rateLimitDelay = Duration.ofMinutes(15)
    }

    override fun close() {
        client.shutdownNow()
    }
}
