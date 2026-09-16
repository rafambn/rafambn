package com.rafambn.profilebanner.counter

import java.nio.file.Files
import java.time.LocalDate
import kotlin.io.path.deleteIfExists
import kotlin.test.Test
import kotlin.test.assertEquals

class ViewStoreTest {
    @Test
    fun incrementsCalculatesWindowsAndSurvivesReopen() {
        val directory = Files.createTempDirectory("profile-banner-views")
        val database = directory.resolve("views.mv.db")
        val scope = "repo:rafambn/kmap"
        val today = LocalDate.of(2026, 9, 14)

        ViewStore(database).use { store ->
            store.increment(scope, today)
            store.increment(scope, today)
            store.increment(scope, today.minusDays(1))
            store.increment(scope, today.minusDays(8))

            assertEquals(
                ViewStats(today = 2, week = 3, month = 4, total = 4),
                store.stats(scope, today)
            )
        }

        ViewStore(database).use { reopened ->
            assertEquals(
                ViewStats(today = 2, week = 3, month = 4, total = 4),
                reopened.stats(scope, today)
            )
        }

        database.deleteIfExists()
        directory.deleteIfExists()
    }
}
