package com.rafambn.profilebanner.web

import com.rafambn.profilebanner.counter.ViewStore
import com.rafambn.profilebanner.github.GitHubStars
import com.rafambn.profilebanner.logging.AppScribe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RoutesTest {
    @Test
    fun sceneLinksAreInteractiveAndRenderingDoesNotIncrementRepositoryViews() {
        val logPath = Path.of("build/test-logs/profile.log")
        Files.createDirectories(logPath.parent)
        AppScribe.configure(logPath)
        val directory = Files.createTempDirectory("profile-cards-routes")
        val database = directory.resolve("views.mv.db")
        try {
            ViewStore(database).use { views ->
                GitHubStars().use { stars ->
                    testApplication {
                        environment { config = MapApplicationConfig() }
                        application { configureRoutes(views, stars) }

                        val home = client.get("/")
                        assertEquals(HttpStatusCode.OK, home.status)
                        assertContains(home.bodyAsText(), "<svg")
                        assertContains(home.bodyAsText(), "href=\"https://github.com/rafambn/KMaP\"")
                        assertContains(home.bodyAsText(), "data-today=\"0\" data-week=\"0\" data-month=\"0\" data-total=\"0\"")
                        assertEquals(0L, views.stats("profile:rafambn").total)
                        assertEquals(0L, views.stats("repo:rafambn/kmap").total)

                        client.get("/badge/rafambn/KMaP.svg")
                        client.get("/badge/RAFAMBN/kmap.svg")
                        val preview = client.get("/preview/launch-base.svg").bodyAsText()
                        assertContains(preview, "data-repository=\"KMaP\" data-stars=\"unknown\" data-views=\"2\"")
                        assertContains(preview, "data-repository=\"Scribe\" data-stars=\"unknown\" data-views=\"0\"")
                        assertEquals(0L, views.stats("profile:rafambn").total)

                        val profile = client.get("/github/profile.svg").bodyAsText()
                        assertContains(profile, "data-views=\"2\"")
                        assertContains(profile, "data-today=\"1\" data-week=\"1\" data-month=\"1\" data-total=\"1\"")
                        assertEquals(1L, views.stats("profile:rafambn").total)
                        assertEquals(2L, views.stats("repo:rafambn/kmap").total)

                        for (path in listOf("/", "/preview/launch-base.svg", "/preview/launch-base.svg?layout=mobile")) {
                            assertContains(client.get(path).bodyAsText(), "data-today=\"1\" data-week=\"1\" data-month=\"1\" data-total=\"1\"")
                        }
                        assertEquals(1L, views.stats("profile:rafambn").total)
                        val mobileProfile = client.get("/github/profile.svg?layout=mobile").bodyAsText()
                        assertContains(mobileProfile, "data-today=\"2\" data-week=\"2\" data-month=\"2\" data-total=\"2\"")
                        assertEquals(2L, views.stats("profile:rafambn").total)
                    }
                }
            }
        } finally {
            database.deleteIfExists()
            directory.deleteIfExists()
        }
    }
}
