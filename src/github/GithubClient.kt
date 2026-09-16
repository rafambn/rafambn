package com.rafambn.profilebanner.github

import com.rafambn.profilebanner.logging.AppScribe
import com.rafambn.profilebanner.pinnedRepos
import com.rafambn.profilebanner.profile.ProfileSnapshot
import com.rafambn.profilebanner.profile.RepositorySnapshot
import com.rafambn.profilebanner.profile.fallbackProfile
import com.rafambn.profilebanner.profile.fallbackRepository
import com.rafambn.scribe.seal
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import org.slf4j.event.Level

private const val CACHE_MILLIS = 15 * 60 * 1000L
private const val GITHUB_API_VERSION = "2026-03-10"
private const val GITHUB_USER_AGENT = "github-profile-banner/0.1"
private const val GITHUB_USER = "rafambn"

class GithubClient : AutoCloseable {
    private val client = HttpClient(CIO) {
        expectSuccess = false
        followRedirects = false
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 4_000
            connectTimeoutMillis = 2_000
            socketTimeoutMillis = 4_000
        }
    }
    private val mutex = Mutex()
    private var cachedAt = 0L
    private var cachedProfile = fallbackProfile()

    suspend fun profile(): ProfileSnapshot = mutex.withLock {
        val now = System.currentTimeMillis()
        if (now - cachedAt < CACHE_MILLIS) {
            return cachedProfile
        }

        cachedProfile = fetchProfile()
        cachedAt = now
        cachedProfile
    }

    override fun close() = client.close()

    private suspend fun fetchProfile(): ProfileSnapshot = coroutineScope {
        val repositories = pinnedRepos
            .map { name -> async { fetchRepository(name) } }
            .awaitAll()
        ProfileSnapshot(repositories)
    }

    private suspend fun fetchRepository(name: String): RepositorySnapshot =
        fetch(name, fallbackRepository(name)) {
            val response: RepositoryResponse = get(
                "https://api.github.com/repos/$GITHUB_USER/$name"
            )
            RepositorySnapshot(
                name = response.name,
                description = response.description.orEmpty(),
                stars = response.stars,
                language = response.language
            )
        }

    private suspend fun <T> fetch(
        repository: String,
        fallback: T,
        request: suspend () -> T
    ): T = try {
        request()
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        if (AppScribe.isEnabled("GithubClient", Level.WARN, null)) {
            val scroll = AppScribe.newScroll()
            scroll["level"] = JsonPrimitive(Level.WARN.name)
            scroll["logger"] = JsonPrimitive("GithubClient")
            scroll["message"] = JsonPrimitive("GitHub request failed")
            scroll["repository"] = JsonPrimitive(repository)
            scroll["exception"] = JsonPrimitive(error.stackTraceToString())
            scroll.seal(AppScribe)
        }
        fallback
    }

    private suspend inline fun <reified T> get(url: String): T {
        val response = client.get(url) {
            header(HttpHeaders.Accept, "application/vnd.github+json")
            header("X-GitHub-Api-Version", GITHUB_API_VERSION)
            header(HttpHeaders.UserAgent, GITHUB_USER_AGENT)
        }
        check(response.status.value in 200..299) {
            "GitHub returned ${response.status} for $url"
        }
        return response.body()
    }

    @Serializable
    private data class RepositoryResponse(
        val name: String,
        val description: String? = null,
        @SerialName("stargazers_count") val stars: Long,
        val language: String? = null
    )
}
