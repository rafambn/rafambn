package com.rafambn.profilebanner.render

import java.util.Locale

internal fun escapeXml(value: String): String = value
    .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    .replace("\"", "&quot;").replace("'", "&apos;")

internal fun formatNumber(number: Long): String =
    number.toString().reversed().chunked(3).joinToString(",").reversed()

internal fun compactCount(count: Long): String {
    if (count < 10_000) return formatNumber(count)
    val (divisor, suffix) = when {
        count >= 1_000_000_000_000_000_000L -> 1e18 to "E"
        count >= 1_000_000_000_000_000L -> 1e15 to "P"
        count >= 1_000_000_000_000L -> 1e12 to "T"
        count >= 1_000_000_000L -> 1e9 to "B"
        count >= 1_000_000L -> 1e6 to "M"
        else -> 1e3 to "k"
    }
    return String.format(Locale.ROOT, "%.1f", count / divisor).removeSuffix(".0") + suffix
}
