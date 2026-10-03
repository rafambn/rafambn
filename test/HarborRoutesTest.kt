package com.rafambn.profilebanner.web

import com.rafambn.profilebanner.counter.ViewStats
import com.rafambn.profilebanner.counter.ViewStore
import com.rafambn.profilebanner.github.GitHubStars
import com.rafambn.profilebanner.logging.AppScribe
import com.rafambn.profilebanner.pinnedRepos
import com.rafambn.profilebanner.render.renderReadmeImages
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.io.path.deleteIfExists
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class HarborRoutesTest {
    @Test
    fun publicImagesCountTheRightScopeAndPreviewsKeepPersistedDataIntact() {
        val log = Path.of("build/test-logs/profile.log")
        Files.createDirectories(log.parent)
        AppScribe.configure(log)
        val directory = Files.createTempDirectory("harbor-routes")
        val database = directory.resolve("views.mv.db")
        val profileScope = "profile:rafambn"
        val today = LocalDate.now(ZoneOffset.UTC)
        try {
            ViewStore(database).use { views ->
                for (daysAgo in listOf(31L, 20L, 6L, 0L)) views.increment(profileScope, today.minusDays(daysAgo))
                repeat(2) { views.increment("repo:rafambn/kmap") }
                GitHubStars().use { stars ->
                    testApplication {
                        environment { config = MapApplicationConfig() }
                        application { configureRoutes(views, stars) }
                        val initial = ViewStats(1, 2, 3, 4)
                        val previewPaths = listOf(
                            "/preview", "/preview/images", "/api/stats",
                            "/preview/harbor.svg", "/preview/harbor.svg?layout=mobile",
                            "/preview/header.svg", "/preview/footer.svg"
                        ) + pinnedRepos.flatMap { listOf("/preview/projects/$it.svg", "/preview/projects/$it.svg?layout=mobile") }
                        for (path in previewPaths) {
                            val response = client.get(path)
                            assertEquals(HttpStatusCode.OK, response.status, path)
                            assertContains(response.headers["Cache-Control"].orEmpty(), "no-store")
                        }
                        assertEquals(initial, views.stats(profileScope))
                        assertEquals(2L, views.stats("repo:rafambn/kmap").total)
                        val json = Json.parseToJsonElement(client.get("/api/stats").bodyAsText()).jsonObject
                        assertEquals("4", json.getValue("profile").jsonObject.getValue("total").toString())
                        assertContains(client.get("/preview/header.svg").bodyAsText(), "data-today=\"1\" data-week=\"2\" data-month=\"3\" data-total=\"4\"")

                        val header = client.get("/github/header.svg")
                        assertContains(header.bodyAsText(), "data-today=\"2\" data-week=\"3\" data-month=\"4\" data-total=\"5\"")
                        for (name in pinnedRepos) {
                            val image = client.get("/github/projects/$name.svg")
                            assertEquals(HttpStatusCode.OK, image.status)
                            assertContains(image.bodyAsText(), "data-views=\"${if (name == "KMaP") 3 else 1}\"")
                        }
                        client.get("/github/footer.svg")
                        assertEquals(5L, views.stats(profileScope).total, "Six project crops must not add six profile views")

                        val home = client.get("/").bodyAsText()
                        assertContains(home, "data-total=\"6\"")
                        assertContains(home, "harbor-mobile")
                        assertContains(client.get("/github/profile.svg?layout=mobile").bodyAsText(), "data-total=\"7\"")
                        assertEquals(7L, views.stats(profileScope).total)
                        assertEquals(3L, views.stats("repo:rafambn/kmap").total)

                        assertEquals(HttpStatusCode.BadRequest, client.get("/github/header.svg?layout=desktop").status)
                        assertEquals(HttpStatusCode.BadRequest, client.get("/github/profile.svg?layout=invalid").status)
                        assertEquals(HttpStatusCode.NotFound, client.get("/github/projects/unknown.svg").status)
                        assertEquals(HttpStatusCode.NotFound, client.get("/badge/other/KMaP.svg").status)
                        assertEquals(7L, views.stats(profileScope).total)
                        assertEquals(3L, views.stats("repo:rafambn/kmap").total)
                        assertEquals(HttpStatusCode.OK, client.get("/badge/RAFAMBN/kmap.svg").status)
                        assertEquals(4L, views.stats("repo:rafambn/kmap").total)
                    }
                }
            }
            ViewStore(database).use { reopened ->
                assertEquals(7L, reopened.stats(profileScope).total)
                assertEquals(4L, reopened.stats("repo:rafambn/kmap").total)
            }
        } finally {
            database.deleteIfExists()
            directory.deleteIfExists()
        }
    }

    @Test
    fun readmeUsesThePublicImageContractWithExternalLinksAndMobileSources() {
        val readme = Files.readString(Path.of("README.md"))
        assertContains(readme, renderReadmeImages("https://profile.rafambn.com/github"))
        assertEquals(8, Regex("<img ").findAll(readme).count())
        assertEquals(8, Regex("<source ").findAll(readme).count())
        for (name in pinnedRepos) assertContains(readme, "href=\"https://github.com/rafambn/$name\"")
        assertFalse(readme.contains("<style"))
        assertFalse(readme.contains("style="))
    }
}
