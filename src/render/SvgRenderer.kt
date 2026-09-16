package com.rafambn.profilebanner.render

import com.rafambn.profilebanner.counter.ViewStats
import com.rafambn.profilebanner.profile.ProfileSnapshot
import com.rafambn.profilebanner.profile.RepositorySnapshot
import java.util.Locale
import kotlinx.css.CssBuilder
import kotlinx.css.animation
import kotlinx.css.animationDelay
import kotlinx.css.keyframes
import kotlinx.css.opacity
import kotlinx.css.properties.Animation
import kotlinx.css.properties.Animations
import kotlinx.css.properties.FillMode
import kotlinx.css.properties.cubicBezier
import kotlinx.css.properties.ms

private val profileAnimationCss = CssBuilder().apply {
    val entranceTiming = cubicBezier(0.22, 1.0, 0.36, 1.0)

    ".metric-card, .repository-card" {
        put("transform-box", "fill-box")
        put("transform-origin", "center")
    }
    ".metric-card" {
        animation += Animation(
            name = "metric-enter",
            duration = 460.ms,
            timing = entranceTiming,
            fillMode = FillMode.both
        )
    }
    repeat(4) { index ->
        ".metric-card-$index" {
            animationDelay = (index * 55).ms
        }
    }
    ".repository-card" {
        animation += Animation(
            name = "repository-enter",
            duration = 520.ms,
            timing = entranceTiming,
            fillMode = FillMode.both
        )
    }
    repeat(6) { index ->
        ".repository-card-$index" {
            animationDelay = (180 + index * 55).ms
        }
    }

    keyframes("metric-enter") {
        from {
            opacity = 0
            put("transform", "translateY(-8px)")
        }
        to {
            opacity = 1
            put("transform", "translateY(0)")
        }
    }
    keyframes("repository-enter") {
        from {
            opacity = 0
            put("transform", "translateY(10px) scale(0.985)")
        }
        to {
            opacity = 1
            put("transform", "translateY(0) scale(1)")
        }
    }
    keyframes("reduced-enter") {
        from { opacity = 0.65 }
        to { opacity = 1 }
    }

    media("(prefers-reduced-motion: reduce)") {
        ".metric-card, .repository-card" {
            animation = Animations().apply {
                this += Animation(
                    name = "reduced-enter",
                    duration = 120.ms,
                    fillMode = FillMode.both
                )
            }
        }
    }
}.toString()

fun renderProfileSvg(
    profile: ProfileSnapshot,
    profileStats: ViewStats,
    repositoryViews: List<Long>,
    mobile: Boolean
): String {
    val width = if (mobile) 720 else 1200
    val height = if (mobile) 1548 else 662
    val repositoryY = 162
    val repositoryWidth = if (mobile) 648 else 348
    val repositoryHeight = if (mobile) 210 else 220
    val repositoryGap = if (mobile) 18 else 24
    val innerWidth = width - 2
    val innerHeight = height - 2
    val animationCss = profileAnimationCss.prependIndent("        ")

    val svg = StringBuilder()
    svg.append(
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <svg xmlns="http://www.w3.org/2000/svg" width="$width" height="$height" viewBox="0 0 $width $height" role="img" aria-labelledby="title description">
          <title id="title">GitHub profile metrics</title>
          <desc id="description">Views and pinned repositories.</desc>
          <style>
            .metric-label { fill: #6b7280; font: 700 10px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; letter-spacing: 1.5px; }
            .metric-value { fill: #111827; font: 800 24px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
            .repo-name { fill: #111827; font: 800 17px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
            .repo-description { fill: #6b7280; font: 400 12px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
            .repo-meta { fill: #6b7280; font: 700 11px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
            .repo-icon-label { fill: #ffffff; font: 800 16px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
            $animationCss
          </style>
          <rect width="100%" height="100%" rx="28" fill="#ffffff"/>
          <rect x="1" y="1" width="$innerWidth" height="$innerHeight" rx="27" fill="none" stroke="#e5e7eb" stroke-width="2"/>
        """.trimIndent()
    )

    renderMetrics(svg, profileStats, width, mobile)
    profile.repositories.take(6).forEachIndexed { index, repository ->
        val x: Int
        val y: Int
        if (mobile) {
            x = 36
            y = repositoryY + index * (repositoryHeight + repositoryGap)
        } else {
            x = 36 + (index % 3) * (repositoryWidth + repositoryGap)
            y = repositoryY + (index / 3) * (repositoryHeight + repositoryGap)
        }
        svg.append("<g class=\"repository-card repository-card-$index\">\n")
        renderRepositoryCard(
            svg = svg,
            x = x,
            y = y,
            width = repositoryWidth,
            height = repositoryHeight,
            repository = repository,
            views = repositoryViews.getOrElse(index) { 0 }
        )
        svg.append("</g>\n")
    }

    svg.append("</svg>")
    return svg.toString()
}

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
        "scribe" -> "#351b29" to "#ffe3ef"
        "wg-kotlin" -> "#1d2344" to "#e5eaff"
        else -> "#202124" to "#ffffff"
    }
    val surface = badgeColor(background, defaultBackground)
    val foreground = badgeColor(textColor, defaultText)
    val numberX = 90
    val numberWidth = formattedViews.length * 8
    val width = numberX + numberWidth + 14
    val pattern = when (name) {
        "scribe" -> "<path d=\"M6 7H42 M6 13H34 M6 19H42 M6 25H26\"/>"
        "wg-kotlin" -> "<path d=\"M0 16H10L18 6H30L38 16H48 M10 16 18 26H30L38 16\"/>"
        else -> ""
    }
    val backgroundSvg = when (name) {
        "kmap" -> renderKmapBackground(width, surface)
        "framebar" -> renderFramebarBackground(width, surface)
        "kflate" -> renderKflateBackground(width, surface)
        "keymanager" -> renderKeymanagerBackground(width, surface)
        else -> """<rect width="$width" height="32" rx="6" fill="url(#theme)"/>"""
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
            <pattern id="theme" width="48" height="32" patternUnits="userSpaceOnUse">
              <g fill="none" stroke="$foreground" stroke-width="1" opacity="0.08">$pattern</g>
            </pattern>
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

private fun renderMetrics(
    svg: StringBuilder,
    stats: ViewStats,
    width: Int,
    mobile: Boolean
) {
    val labels = listOf(
        "TODAY" to stats.today,
        "THIS WEEK" to stats.week,
        "THIS MONTH" to stats.month,
        "ALL TIME" to stats.total
    )
    val x = 36
    val gap = if (mobile) 12 else 16
    val cardWidth = if (mobile) 153 else (width - 72 - 48) / 4
    val cardHeight = if (mobile) 108 else 104
    val cardY = 36

    labels.forEachIndexed { index, (label, value) ->
        val cardX = x + index * (cardWidth + gap)
        svg.append("<g class=\"metric-card metric-card-$index\">\n")
        svg.append(
            """<rect x="$cardX" y="$cardY" width="$cardWidth" height="$cardHeight" rx="18" fill="#fafafa" stroke="#e5e7eb"/>
            """.trimIndent()
        )
        svg.append(svgText(cardX + 16, cardY + 29, "metric-label", label))
        svg.append(svgText(cardX + 16, cardY + 70, "metric-value", formatNumber(value)))
        svg.append("</g>\n")
    }
}

private fun renderRepositoryCard(
    svg: StringBuilder,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    repository: RepositorySnapshot,
    views: Long
) {
    val color = languageColor(repository.language)
    val circleX = x + 32
    val circleY = y + 34
    svg.append(
        """<rect x="$x" y="$y" width="$width" height="$height" rx="22" fill="#ffffff" stroke="#dbeafe" stroke-width="2"/>
        <circle cx="$circleX" cy="$circleY" r="21" fill="$color"/>
        """.trimIndent()
    )
    svg.append(centeredSvgText(x + 32, y + 40, "repo-icon-label", initial(repository.name)))
    svg.append(svgText(x + 66, y + 39, "repo-name", truncate(repository.name, 24)))

    val description = truncate(
        if (repository.description.isEmpty()) "Pinned repository" else repository.description,
        if (width > 500) 78 else 38
    )
    val (firstLine, secondLine) = splitDescription(description)
    svg.append(svgText(x + 24, y + 86, "repo-description", firstLine))
    if (secondLine.isNotEmpty()) {
        svg.append(svgText(x + 24, y + 108, "repo-description", secondLine))
    }
    svg.append(
        svgText(
            x + 24,
            y + height - 25,
            "repo-meta",
            "★ " + formatNumber(repository.stars)
        )
    )
    svg.append(
        svgTextAnchor(
            x + width - 24,
            y + height - 25,
            "repo-meta",
            "end",
            "views " + formatNumber(views)
        )
    )
}

private fun svgText(x: Int, y: Int, className: String, value: String): String =
    "<text x=\"" + x + "\" y=\"" + y + "\" class=\"" + className + "\">" +
        escapeXml(value) + "</text>\n"

private fun svgTextAnchor(
    x: Int,
    y: Int,
    className: String,
    anchor: String,
    value: String
): String =
    "<text x=\"" + x + "\" y=\"" + y + "\" text-anchor=\"" + anchor +
        "\" class=\"" + className + "\">" + escapeXml(value) + "</text>\n"

private fun centeredSvgText(x: Int, y: Int, className: String, value: String): String =
    "<text x=\"" + x + "\" y=\"" + y + "\" text-anchor=\"middle\" class=\"" +
        className + "\">" + escapeXml(value) + "</text>\n"

private fun splitDescription(description: String): Pair<String, String> {
    description.indexOf('\n').takeIf { it >= 0 }?.let { newline ->
        return description.substring(0, newline) to description.substring(newline + 1)
    }
    if (description.codePointCount(0, description.length) <= 42) {
        return description to ""
    }

    val codePointBoundary = description.offsetByCodePoints(0, 42)
    val whitespaceBoundary = description
        .substring(0, codePointBoundary)
        .indexOfLast(Char::isWhitespace)
    val boundary = if (whitespaceBoundary > 0) whitespaceBoundary else codePointBoundary
    return description.substring(0, boundary).trim() to
        description.substring(boundary).trim()
}

private fun truncate(value: String, maxChars: Int): String {
    if (value.codePointCount(0, value.length) <= maxChars) {
        return value
    }
    val end = value.offsetByCodePoints(0, maxChars - 1)
    return value.substring(0, end) + "…"
}

private fun formatNumber(number: Long): String =
    number.toString().reversed().chunked(3).joinToString(".").reversed()

private fun initial(value: String): String {
    if (value.isEmpty()) {
        return "R"
    }
    val codePoint = value.codePointAt(0)
    return String(Character.toChars(codePoint)).uppercase(Locale.ROOT)
}

private fun languageColor(language: String?): String =
    when (language.orEmpty().lowercase(Locale.ROOT)) {
        "kotlin" -> "#7f52ff"
        "rust" -> "#dea584"
        "typescript" -> "#3178c6"
        "javascript" -> "#f1e05a"
        "swift" -> "#f05138"
        "python" -> "#3572a5"
        "java" -> "#b07219"
        else -> "#7c3aed"
    }

private fun escapeXml(value: String): String =
    value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
