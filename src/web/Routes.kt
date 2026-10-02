package com.rafambn.profilebanner.web

import com.rafambn.profilebanner.counter.ViewStore
import com.rafambn.profilebanner.RepositoryStats
import com.rafambn.profilebanner.github.GitHubStars
import com.rafambn.profilebanner.pinnedRepos
import com.rafambn.profilebanner.render.renderLaunchBaseSvg
import com.rafambn.profilebanner.render.renderRepositoryBadge
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.Locale

private const val PROFILE_USER = "rafambn"

fun Application.configureRoutes(views: ViewStore, stars: GitHubStars) {
    fun repositoryStats(): Map<String, RepositoryStats> {
        val starCounts = stars.counts
        return pinnedRepos.associateWith { repository ->
            RepositoryStats(starCounts[repository], views.stats(repoScope(PROFILE_USER, repository)).total)
        }
    }

    routing {
        get("/") {
            call.response.headers.append(HttpHeaders.CacheControl, "no-store")
            val scene = renderLaunchBaseSvg(repositoryStats = repositoryStats()).substringAfter("?>")
            call.respondText(
                """<!doctype html>
                <html lang="en"><head><meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Rafael — Launch base</title>
                <style>html,body{margin:0;background:#fff}main{display:block}svg{max-width:100%}</style>
                </head><body><main aria-label="Rafael's open source repositories">$scene</main></body></html>""".trimIndent(),
                ContentType.Text.Html
            )
        }

        get("/preview/launch-base.svg") {
            call.respondSvg(renderLaunchBaseSvg(
                mobile = call.request.queryParameters["layout"] == "mobile",
                repositoryStats = repositoryStats()
            ))
        }

        get("/github/profile.svg") {
            views.increment(profileScope(PROFILE_USER))
            val mobile = call.request.queryParameters["layout"]
                ?.equals("mobile", ignoreCase = true) == true
            call.respondSvg(renderLaunchBaseSvg(mobile = mobile, repositoryStats = repositoryStats()))
        }

        get("/badge/{owner}/{repository}") {
            val owner = call.parameters["owner"]
            val repository = call.parameters["repository"]
                ?.removeSuffix(".svg")
            if (owner == null || repository == null ||
                !isAllowedRepository(owner, repository)
            ) {
                call.respond(
                    HttpStatusCode.NotFound,
                    "This repository badge is not enabled"
                )
                return@get
            }

            val stats = views.increment(repoScope(owner, repository))
            call.respondSvg(
                renderRepositoryBadge(
                    repository = "$owner/$repository",
                    totalViews = stats.total,
                    background = call.request.queryParameters["background"],
                    textColor = call.request.queryParameters["textColor"]
                )
            )
        }
    }
}

private suspend fun ApplicationCall.respondSvg(svg: String) {
    response.headers.append(
        HttpHeaders.CacheControl,
        "no-store, max-age=0, must-revalidate"
    )
    response.headers.append("X-Content-Type-Options", "nosniff")
    respondText(svg, ContentType("image", "svg+xml"))
}

private fun isAllowedRepository(owner: String, repository: String): Boolean =
    owner.equals(PROFILE_USER, ignoreCase = true) &&
        pinnedRepos.any { it.equals(repository, ignoreCase = true) }

private fun profileScope(user: String): String =
    "profile:" + user.lowercase(Locale.ROOT)

private fun repoScope(owner: String, repository: String): String =
    "repo:" + owner.lowercase(Locale.ROOT) + "/" + repository.lowercase(Locale.ROOT)
