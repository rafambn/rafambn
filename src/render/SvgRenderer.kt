package com.rafambn.profilebanner.render

import java.util.Locale

fun renderRepositoryBadge(
    repository: String,
    totalViews: Long,
    background: String? = null,
    textColor: String? = null
): String {
    val formattedViews = formatNumber(totalViews)
    val safeRepository = escapeXml(repository)
    val name = repository.substringAfterLast("/").lowercase(Locale.ROOT)
    val (defaultBackground, defaultText) = when (name) {
        "kmap" -> "#e8eddb" to "#173b43"
        "framebar" -> "#202226" to "#f5f5f4"
        "kflate" -> "#0c1b2b" to "#f1f2f6"
        "keymanager" -> "#071d2b" to "#e3f2ff"
        "scribe" -> "#f5ead2" to "#443522"
        "wg-kotlin" -> "#2d3748" to "#fff7ed"
        else -> "#202124" to "#ffffff"
    }
    val surface = badgeColor(background, defaultBackground)
    val foreground = badgeColor(textColor, defaultText)
    val numberX = 90
    val numberWidth = formattedViews.length * 8
    val width = numberX + numberWidth + 14
    val backgroundSvg = when (name) {
        "kmap" -> renderKmapBackground(width, surface)
        "framebar" -> renderFramebarBackground(width, surface)
        "kflate" -> renderKflateBackground(width, surface)
        "keymanager" -> renderKeymanagerBackground(width, surface)
        "scribe" -> renderScribeBackground(width, surface)
        "wg-kotlin" -> renderWgKotlinBackground(width, surface)
        else -> ""
    }
    val textOutline = if (name == "kmap") {
        """stroke="#ffffff" stroke-opacity="0.92" stroke-width="2.5" stroke-linejoin="round" paint-order="stroke fill" """
    } else {
        ""
    }
    return """
        <?xml version="1.0" encoding="UTF-8"?>
        <svg xmlns="http://www.w3.org/2000/svg" width="$width" height="32" viewBox="0 0 $width 32" role="img" aria-labelledby="title">
          <title id="title">$safeRepository views: $formattedViews</title>
          <defs>
            <clipPath id="badge-clip"><rect width="$width" height="32" rx="6"/></clipPath>
          </defs>
          <rect width="$width" height="32" rx="6" fill="$surface"/>
          ${backgroundSvg.replace("\n", "")}
          <g font-family="ui-monospace, SFMono-Regular, Menlo, Consolas, monospace" font-size="13" font-weight="600" fill="$foreground" text-anchor="start" $textOutline>
            <text x="14" y="21">views</text>
            <text x="$numberX" y="21" textLength="$numberWidth" lengthAdjust="spacingAndGlyphs">$formattedViews</text>
          </g>
        </svg>
    """.trimIndent()
}

private fun badgeColor(value: String?, fallback: String): String {
    val hex = value?.removePrefix("#") ?: return fallback
    return if (hex.matches(Regex("[0-9a-fA-F]{6}"))) "#$hex" else fallback
}

internal fun formatNumber(number: Long): String =
    number.toString().reversed().chunked(3).joinToString(".").reversed()

internal fun escapeXml(value: String): String =
    value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
