package com.rafambn.profilebanner.web

import com.rafambn.profilebanner.counter.ViewStore
import com.rafambn.profilebanner.github.GithubClient
import com.rafambn.profilebanner.pinnedRepos
import com.rafambn.profilebanner.render.renderProfileSvg
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

fun Application.configureRoutes(views: ViewStore, github: GithubClient) {
    routing {
        get("/github/profile.svg") {
            val profileStats = views.increment(profileScope(PROFILE_USER))
            val profile = github.profile()
            val mobile = call.request.queryParameters["layout"]
                ?.equals("mobile", ignoreCase = true) == true
            val repositoryViews = profile.repositories
                .take(6)
                .map { repository ->
                    views.stats(
                        repoScope(PROFILE_USER, repository.name)
                    ).total
                }

            call.respondSvg(
                renderProfileSvg(
                    profile = profile,
                    profileStats = profileStats,
                    repositoryViews = repositoryViews,
                    mobile = mobile
                )
            )
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
