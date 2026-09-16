package com.rafambn.profilebanner.counter

import org.h2.mvstore.MVMap
import org.h2.mvstore.MVStore
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.ZoneOffset

private const val WEEK_WINDOW_DAYS = 7
private const val MONTH_WINDOW_DAYS = 30
private const val DAILY_RETENTION_DAYS = 31L
private const val AUTO_COMMIT_DELAY_MILLIS = 5_000
private const val KEY_SEPARATOR = '|'
private const val TOTALS_MAP_NAME = "view_totals"
private const val DAILY_MAP_NAME = "view_daily"

class ViewStore(path: Path) : AutoCloseable {
    private val store = openStore(path)
    private val totals = store.openMap<String, Long>(TOTALS_MAP_NAME)
    private val daily = store.openMap<String, Long>(DAILY_MAP_NAME)
    private var isClosed = false

    @Synchronized
    fun increment(scope: String, today: LocalDate = currentDate()): ViewStats {
        check(!isClosed) { "ViewStore is closed" }
        require(scope.isNotBlank()) { "View scope must not be blank" }
        prune(today)

        val total = incrementCounter(totals, scope)
        incrementCounter(daily, dayKey(today, scope))
        return calculateStats(scope, today, total)
    }

    @Synchronized
    fun stats(scope: String, today: LocalDate = currentDate()): ViewStats {
        check(!isClosed) { "ViewStore is closed" }
        require(scope.isNotBlank()) { "View scope must not be blank" }
        return calculateStats(scope, today, totals[scope] ?: 0)
    }

    @Synchronized
    override fun close() {
        if (!isClosed) {
            store.close()
            isClosed = true
        }
    }

    private fun calculateStats(scope: String, today: LocalDate, total: Long): ViewStats {
        val todayViews = daily[dayKey(today, scope)] ?: 0
        var week = todayViews
        var month = todayViews

        for (daysAgo in 1 until MONTH_WINDOW_DAYS) {
            val count = daily[dayKey(today.minusDays(daysAgo.toLong()), scope)] ?: 0
            month += count
            if (daysAgo < WEEK_WINDOW_DAYS) {
                week += count
            }
        }

        return ViewStats(
            today = todayViews,
            week = week,
            month = month,
            total = total
        )
    }

    private fun prune(today: LocalDate) {
        val cutoff = today.minusDays(DAILY_RETENTION_DAYS)
        val expiredKeys = daily.keys
            .toList()
            .filter { key -> isExpired(key, cutoff) }

        if (expiredKeys.isEmpty()) {
            return
        }

        expiredKeys.forEach(daily::remove)
    }

    private fun isExpired(key: String, cutoff: LocalDate): Boolean {
        val separator = key.indexOf(KEY_SEPARATOR)
        if (separator <= 0) {
            return false
        }

        val date = runCatching {
            LocalDate.parse(key.substring(0, separator))
        }.getOrNull() ?: return false
        return date.isBefore(cutoff)
    }

    private fun incrementCounter(map: MVMap<String, Long>, key: String): Long {
        val next = (map[key] ?: 0) + 1
        map[key] = next
        return next
    }

    private fun dayKey(date: LocalDate, scope: String): String = "$date$KEY_SEPARATOR$scope"

    private fun openStore(path: Path): MVStore {
        path.parent?.let(Files::createDirectories)
        return MVStore.Builder()
            .fileName(path.toString())
            .open()
            .apply { autoCommitDelay = AUTO_COMMIT_DELAY_MILLIS }
    }

    private fun currentDate(): LocalDate = LocalDate.now(ZoneOffset.UTC)
}
