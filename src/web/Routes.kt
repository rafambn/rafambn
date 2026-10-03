package com.rafambn.profilebanner.web

import com.rafambn.profilebanner.PortfolioStats
import com.rafambn.profilebanner.RepositoryStats
import com.rafambn.profilebanner.counter.ViewStats
import com.rafambn.profilebanner.counter.ViewStore
import com.rafambn.profilebanner.github.GitHubStars
import com.rafambn.profilebanner.pinnedRepos
import com.rafambn.profilebanner.render.HarborLayout
import com.rafambn.profilebanner.render.renderHarborPage
import com.rafambn.profilebanner.render.renderHarborSvg
import com.rafambn.profilebanner.render.renderImagePreview
import com.rafambn.profilebanner.render.renderRepositoryBadge
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.Locale

private const val PROFILE_USER = "rafambn"

fun Application.configureRoutes(views: ViewStore, stars: GitHubStars) {
    val profileScope = "profile:$PROFILE_USER"
    fun snapshot(profile: ViewStats = views.stats(profileScope)): PortfolioStats {
        val counts = stars.counts
        return PortfolioStats(profile, pinnedRepos.map { name ->
            RepositoryStats(name, counts[name], views.stats(repositoryScope(name)))
        })
    }

    routing {
        get("/") {
            val profile = views.increment(profileScope)
            call.respondUncached(renderHarborPage(snapshot(profile)), ContentType.Text.Html)
        }

        get("/preview") {
            call.respondUncached(renderHarborPage(snapshot()), ContentType.Text.Html)
        }

        get("/preview/images") {
            call.respondUncached(renderImagePreview(), ContentType.Text.Html)
        }

        get("/preview/harbor.svg") {
            val layout = call.harborLayout(HarborLayout.DESKTOP) ?: return@get
            call.respondSvg(renderHarborSvg(snapshot(), layout))
        }

        get("/github/profile.svg") {
            val layout = call.harborLayout(HarborLayout.DESKTOP) ?: return@get
            call.respondSvg(renderHarborSvg(snapshot(views.increment(profileScope)), layout))
        }

        for (prefix in listOf("github", "preview")) {
            get("/$prefix/header.svg") {
                val layout = call.harborLayout(HarborLayout.README, split = true) ?: return@get
                val profile = if (prefix == "github") views.increment(profileScope) else views.stats(profileScope)
                call.respondSvg(renderHarborSvg(snapshot(profile), layout, height = layout.headerHeight))
            }

            get("/$prefix/projects/{repository}") {
                val requested = call.parameters["repository"]?.removeSuffix(".svg")
                val index = pinnedRepos.indexOfFirst { it.equals(requested, ignoreCase = true) }
                if (index < 0) {
                    call.respondText("Repository not found", status = HttpStatusCode.NotFound)
                    return@get
                }
                val layout = call.harborLayout(HarborLayout.README, split = true) ?: return@get
                if (prefix == "github") views.increment(repositoryScope(pinnedRepos[index]))
                call.respondSvg(renderHarborSvg(
                    snapshot(), layout,
                    startY = layout.headerHeight + index * layout.rowHeight,
                    height = layout.rowHeight,
                    projectIndex = index
                ))
            }

            get("/$prefix/footer.svg") {
                val layout = call.harborLayout(HarborLayout.README, split = true) ?: return@get
                call.respondSvg(renderHarborSvg(
                    snapshot(), layout,
                    startY = layout.headerHeight + layout.bodyHeight,
                    height = layout.footerHeight
                ))
            }
        }

        get("/badge/{owner}/{repository}") {
            val requested = call.parameters["repository"]?.removeSuffix(".svg")
            val name = pinnedRepos.find { it.equals(requested, ignoreCase = true) }
            if (!PROFILE_USER.equals(call.parameters["owner"], ignoreCase = true) || name == null) {
                call.respondText("Repository badge not found", status = HttpStatusCode.NotFound)
                return@get
            }
            val stats = views.increment(repositoryScope(name))
            call.respondSvg(renderRepositoryBadge("$PROFILE_USER/$name", stats.total))
        }

        get("/api/stats") {
            val snapshot = snapshot()
            val stats = buildJsonObject {
                put("owner", PROFILE_USER)
                put("profile", snapshot.profile.toJson())
                put("repositories", buildJsonArray {
                    for (repository in snapshot.repositories) {
                        add(buildJsonObject {
                            put("name", repository.name)
                            put("stars", repository.stars?.let(::JsonPrimitive) ?: JsonNull)
                            put("views", repository.views.toJson())
                        })
                    }
                })
            }
            call.respondUncached(stats.toString(), ContentType.Application.Json)
        }
    }
}

private fun repositoryScope(name: String) = "repo:$PROFILE_USER/${name.lowercase(Locale.ROOT)}"

private suspend fun ApplicationCall.harborLayout(default: HarborLayout, split: Boolean = false): HarborLayout? {
    val layout = when (request.queryParameters["layout"]?.lowercase(Locale.ROOT)) {
        null -> default
        "desktop" -> HarborLayout.DESKTOP
        "mobile" -> HarborLayout.MOBILE
        "readme" -> HarborLayout.README
        else -> null
    }
    if (layout == null || split && layout == HarborLayout.DESKTOP) {
        respondText("Use layout=${if (split) "readme or mobile" else "desktop, mobile or readme"}", status = HttpStatusCode.BadRequest)
        return null
    }
    return layout
}

private suspend fun ApplicationCall.respondSvg(svg: String) =
    respondUncached(svg, ContentType("image", "svg+xml"))

private suspend fun ApplicationCall.respondUncached(body: String, contentType: ContentType) {
    response.headers.append(HttpHeaders.CacheControl, "no-store, max-age=0, must-revalidate")
    response.headers.append("X-Content-Type-Options", "nosniff")
    respondText(body, contentType)
}

private fun ViewStats.toJson() = buildJsonObject {
    put("today", today)
    put("week", week)
    put("month", month)
    put("total", total)
}
