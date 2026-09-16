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
    val height = if (mobile) 1282 else 680
    val repositoryY = if (mobile) 422 else 260
    val repositoryWidth = if (mobile) 648 else 348
    val repositoryHeight = if (mobile) 124 else 180
    val repositoryGap = if (mobile) 16 else 24
    val animationCss = profileAnimationCss.prependIndent("        ")
    val mobileCss = if (mobile) """
        .metric-label { font-size: 16px; letter-spacing: 0.8px; }
        .metric-value { font-size: 28px; }
        .repo-name { font-size: 26px; }
        .repo-description { font-size: 20px; }
        .repo-meta { font-size: 20px; }
        .repo-icon-label { font-size: 24px; }
        .profile-bio { font-size: 22px; }
    """.trimIndent().prependIndent("            ") else ""

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
            .profile-name { fill: #111827; font: 750 38px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
            .profile-bio { fill: #4b5563; font: 400 16px -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
            $mobileCss
            $animationCss
          </style>
        """.trimIndent()
    )

    val identityX = if (mobile) width / 2 else 36
    val identityAnchor = if (mobile) "middle" else "start"
    svg.append(svgTextAnchor(identityX, 86, "profile-name", identityAnchor, truncate(profile.name, 27)))
    wrapText(profile.bio, if (mobile) 48 else 70, 3).forEachIndexed { index, line ->
        svg.append(svgTextAnchor(identityX, 124 + index * 24, "profile-bio", identityAnchor, line))
    }
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

private fun renderMetrics(
    svg: StringBuilder,
    stats: ViewStats,
    width: Int,
    mobile: Boolean
) {
    val labels = listOf(
        "TODAY / VIEWS" to stats.today,
        "THIS WEEK / VIEWS" to stats.week,
        "THIS MONTH / VIEWS" to stats.month,
        "TOTAL / VIEWS" to stats.total
    )
    val x = if (mobile) 36 else 744
    val gap = 16
    val cardWidth = if (mobile) (width - 72 - gap) / 2 else 202
    val cardHeight = 86

    labels.forEachIndexed { index, (label, value) ->
        val cardX = x + (index % 2) * (cardWidth + gap)
        val cardY = (if (mobile) 202 else 36) + (index / 2) * (cardHeight + gap)
        svg.append("<g class=\"metric-card metric-card-$index\">\n")
        svg.append(
            """<rect x="$cardX" y="$cardY" width="$cardWidth" height="$cardHeight" rx="18" fill="#fafafa" stroke="#e5e7eb"/>
            """.trimIndent()
        )
        svg.append(svgText(cardX + 16, cardY + 29, "metric-label", label))
        svg.append(svgText(cardX + 16, cardY + 62, "metric-value", formatNumber(value)))
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
    val mobile = width > 500
    val circleX = x + 44
    val circleY = y + if (mobile) height / 2 else 48
    svg.append(
        """<rect x="$x" y="$y" width="$width" height="$height" rx="22" fill="#ffffff" stroke="#dbeafe" stroke-width="2"/>
        <circle cx="$circleX" cy="$circleY" r="28" fill="$color"/>
        """.trimIndent()
    )
    svg.append(centeredSvgText(circleX, circleY + 6, "repo-icon-label", initial(repository.name)))
    svg.append(svgText(x + 88, y + 39, "repo-name", truncate(repository.name, 24)))

    val description = repository.description.ifEmpty { "Pinned repository" }
    wrapText(description, 32, if (mobile) 2 else 3).forEachIndexed { index, line ->
        svg.append(svgText(x + 88, y + 66 + index * (if (mobile) 26 else 18), "repo-description", line))
    }
    svg.append(
        svgTextAnchor(
            if (mobile) x + width - 24 else x + 24,
            y + if (mobile) 43 else height - 24,
            "repo-meta",
            if (mobile) "end" else "start",
            "★ " + formatNumber(repository.stars)
        )
    )
    svg.append(
        svgTextAnchor(
            x + width - 24,
            y + if (mobile) 87 else height - 24,
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

private fun wrapText(value: String, columns: Int, maxLines: Int): List<String> {
    var remaining = value.trim().replace(Regex("\\s+"), " ")
    val lines = mutableListOf<String>()
    while (remaining.isNotEmpty() && lines.size < maxLines) {
        if (lines.size == maxLines - 1) {
            lines += truncate(remaining, columns)
            break
        }
        if (remaining.codePointCount(0, remaining.length) <= columns) {
            lines += remaining
            break
        }
        val limit = remaining.offsetByCodePoints(0, columns)
        val boundary = remaining.lastIndexOf(' ', limit).takeIf { it > 0 } ?: limit
        lines += remaining.substring(0, boundary)
        remaining = remaining.substring(boundary).trimStart()
    }
    return lines
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
