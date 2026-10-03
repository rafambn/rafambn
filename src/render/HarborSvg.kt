package com.rafambn.profilebanner.render

import com.rafambn.profilebanner.PortfolioStats
import com.rafambn.profilebanner.counter.ViewStats

fun renderHarborSvg(
    stats: PortfolioStats,
    layout: HarborLayout = HarborLayout.DESKTOP,
    startY: Int = 0,
    height: Int = layout.height,
    projectIndex: Int? = null
): String {
    val namespace = "harbor-${layout.name.lowercase()}-"
    val title = projectIndex?.let { "${stats.repositories[it].name} · Rafael Mendonça" }
        ?: "Rafael Mendonça · Open-source harbor"
    val svg = buildString {
        append("""<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" class="harbor-scene" width="${layout.width}" height="$height" viewBox="0 $startY ${layout.width} $height" role="group" aria-labelledby="harbor-title harbor-desc">
<title id="harbor-title">${escapeXml(title)}</title>
<desc id="harbor-desc">An overhead harbor with one cargo ship on the left and linked project containers on the quay. Solutions architect. Making complex things simple. Stars come from GitHub; views are recorded image and page requests.</desc>
$harborDefinitions
""")
        if (startY < layout.headerHeight) append(harborHeadingFonts)
        append(drawHarborGround(layout))
        if (startY < layout.headerHeight) append(drawProfileHeader(stats.profile, layout))
        if (startY + height > layout.headerHeight && startY < layout.headerHeight + layout.bodyHeight) {
            append(drawCargoShip(layout))
            if (projectIndex != null) {
                append(drawProject(stats.repositories[projectIndex], projectIndex, layout))
            } else {
                stats.repositories.forEachIndexed { index, repository -> append(drawProject(repository, index, layout)) }
            }
            append(drawHarborCranes(layout))
        }
        if (startY + height > layout.headerHeight + layout.bodyHeight) append(drawHarborFooter(layout))
        append("</svg>")
    }
    return svg.replace(Regex(">\\s+<"), "><")
        .replace("id=\"harbor-", "id=\"$namespace")
        .replace("#harbor-", "#$namespace")
        .replace("aria-labelledby=\"harbor-title harbor-desc\"", "aria-labelledby=\"${namespace}title ${namespace}desc\"")
}

private fun drawProfileHeader(stats: ViewStats, layout: HarborLayout): String = buildString {
    val mobile = layout == HarborLayout.MOBILE
    val desktop = layout == HarborLayout.DESKTOP
    val left = if (mobile) 16 else 32
    append("""
<g data-section="profile">
 <rect width="${if (mobile) layout.width else layout.quayEnd - 4}" height="${layout.headerHeight}" fill="#fcf5e2"/>
 <text class="profile-name" role="heading" aria-level="1" x="$left" y="${if (mobile) 48 else 80}" font-size="${if (mobile) 36 else if (desktop) 64 else 63}" letter-spacing="${if (mobile) -.8 else -1.5}">Rafael Mendonça</text>
""")
    if (mobile) {
        append("""<text class="profile-sentence" x="$left" y="78" font-size="17"><tspan x="$left">Solutions architect.</tspan><tspan x="$left" dy="23">Making complex things simple.</tspan></text>""")
    } else {
        append("""<text class="profile-sentence" x="$left" y="116" font-size="${if (desktop) 23 else 22}">Solutions architect. Making complex things simple.</text>""")
    }
    val statsX = if (desktop) 731 else left
    val columnWidth = if (desktop) 75 else ((if (mobile) layout.width else layout.quayEnd) - left * 2) / 4
    val labelY = if (mobile) 147 else if (desktop) 66 else 156
    val valueY = labelY + if (mobile) 34 else 36
    val exact = "Profile views: ${formatNumber(stats.today)} today in UTC, ${formatNumber(stats.week)} in the last 7 days, ${formatNumber(stats.month)} in the last 30 days, ${formatNumber(stats.total)} total"
    append("""<g role="img" aria-label="$exact" data-profile-views="true" data-today="${stats.today}" data-week="${stats.week}" data-month="${stats.month}" data-total="${stats.total}"><title>$exact</title><g aria-hidden="true">""")
    listOf("Today" to stats.today, "Week" to stats.week, "Month" to stats.month, "Total" to stats.total).forEachIndexed { index, (label, value) ->
        val x = statsX + index * columnWidth
        val count = compactCount(value)
        val valueSize = (if (mobile) 28 else 32) - if (count.length > 5) 5 else 0
        append("""<text x="$x" y="$labelY" font-size="${if (mobile) 16 else 18}">$label</text><text x="$x" y="$valueY" font-size="$valueSize">$count</text>""")
        if (index > 0) append("""<path d="M${x - if (mobile) 13 else 18} ${labelY - 12}V${valueY + 2}" stroke="#d3cab4"/>""")
    }
    if (desktop) append("""<text x="$statsX" y="137" font-size="16">Profile views</text>""")
    append("</g></g></g>")
}

private fun drawHarborFooter(layout: HarborLayout): String {
    val x = layout.projectStart
    val y = layout.headerHeight + layout.bodyHeight
    return """
<a href="https://github.com/rafambn" aria-label="Visit Rafael Mendonça on GitHub" tabindex="0" transform="translate($x $y)">
 <rect x="-8" y="8" width="194" height="48" fill="transparent"/>
 <rect class="focus-ring" x="-7" y="9" width="192" height="46" rx="2" fill="none" stroke="#062c43" stroke-width="2"/>
 <text x="0" y="40" font-size="${if (layout == HarborLayout.MOBILE) 20 else 22}">GitHub / rafambn</text>
 <use href="#harbor-arrow" x="150" y="23"/>
</a>
"""
}
