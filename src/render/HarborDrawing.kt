package com.rafambn.profilebanner.render

import com.rafambn.profilebanner.RepositoryStats

internal fun drawHarborGround(layout: HarborLayout): String = buildString {
    val mobile = layout == HarborLayout.MOBILE
    val sea = layout.seaWidth
    val end = layout.quayEnd
    val height = layout.bodyHeight + layout.footerHeight
    append("""
<g aria-hidden="true">
 <g transform="translate(0 ${layout.headerHeight})">
  <rect width="${layout.width}" height="$height" fill="url(#harbor-concrete)"/>
  <rect width="$sea" height="$height" fill="url(#harbor-water)"/>
  <path class="water-current" d="M12 0Q26 90 12 180T12 360T12 540T12 720T12 900T12 1080T12 1260T12 1440T12 1620" fill="none" stroke="#90e1e4" stroke-width=".9" stroke-dasharray="7 173" opacity=".55"/>
  <path d="M${sea - 3} 0V$height" stroke="#193f48" stroke-width="8"/>
  <path d="M${sea + 3} 0V$height" stroke="#e6debd" stroke-width="5"/>
  <path d="M${sea + 11} 0V$height" stroke="#777e73" stroke-width="1.4"/>
""")
    for (row in 0 until layout.rows) {
        val y = row * layout.rowHeight
        append("""<path d="M${sea + 14} ${y + 13}H${end - 8}M${sea + 14} ${y + layout.rowHeight - 9}H${end - 8}" fill="none" stroke="#e9c74b" stroke-width="${if (mobile) 1 else 1.8}"/>""")
        append("""<use href="#harbor-bollard" x="${sea + 8}" y="${y + 10}"/><use href="#harbor-bollard" x="${end - 13}" y="${y + layout.rowHeight - 10}"/>""")
        for (column in 0 until layout.columns) {
            val x = layout.projectStart + column * (layout.projectWidth + layout.projectGap)
            val top = if (mobile) 26 else 40
            val roofHeight = layout.rowHeight - top * 2
            val bayLeft = x - if (mobile) 6 else 11
            val bayWidth = layout.projectWidth + if (mobile) 12 else 22
            append("""<rect x="$bayLeft" y="${y + top - 13}" width="$bayWidth" height="${roofHeight + 26}" fill="none" stroke="#edc950" stroke-width="${if (mobile) 1 else 1.8}"/>""")
            if (!mobile) {
                append("""<rect x="${x + layout.projectWidth + 13}" y="${y + top - 13}" width="18" height="${roofHeight + 26}" fill="url(#harbor-hatching)" stroke="#edc950" stroke-width="1.2"/>""")
            }
        }
        val scale = if (mobile) .48 else .85
        val cargoX = layout.projectStart - if (mobile) 17 else 35
        val cargoY = y + if (mobile) 91 else 136
        val cargoColor = if (row % 2 == 0) "#1787b7" else "#da6136"
        append("""
 <use href="#harbor-cargo" x="$cargoX" y="$cargoY" width="${18 * scale}" height="${43 * scale}" style="color:$cargoColor"/>
 <use href="#harbor-cargo" x="${cargoX + 1}" y="${cargoY + 47 * scale}" width="${17 * scale}" height="${35 * scale}" style="color:#2187ac"/>
 <use href="#harbor-pallet" transform="translate($cargoX ${cargoY + 85 * scale}) scale($scale)"/>
 <use href="#harbor-drain" x="${sea + 25}" y="${y + 21}"/>
""")
        if (!mobile) {
            val rightX = end - 22
            append("""<use href="#harbor-vehicle" x="$rightX" y="${y + layout.rowHeight - 55}" style="color:${if (row % 3 == 1) "#f7f3db" else "#f1c231"}"/>""")
            if (row % 3 == 1) {
                append("""<use href="#harbor-pipes" x="$rightX" y="${y + 56}"/>""")
            } else {
                append("""<use href="#harbor-cargo" x="${rightX - 6}" y="${y + 56}" width="20" height="43" style="color:${if (row % 2 == 0) "#d65b35" else "#1787b7"}"/>""")
            }
            append("""<use href="#harbor-drain" x="${end - 49}" y="${y + layout.rowHeight - 1}"/>""")
        }
    }
    for (y in 27 until height - 24 step if (mobile) 67 else 81) {
        append("""<rect x="${sea - 7}" y="$y" width="8" height="${if (mobile) 16 else 23}" rx="1" fill="#263b3c" stroke="#0c303b"/><path d="M${sea - 5} ${y + 2}V${y + if (mobile) 14 else 21}" stroke="#63716b" stroke-width="1.5"/>""")
    }
    val stepY = layout.bodyHeight + if (mobile) 52 else 63
    val stepWidth = if (mobile) 17 else 45
    append("""
 <path d="M0 $stepY H${sea + stepWidth}V${stepY + 23}H${sea + stepWidth * 2}V$height H0Z" fill="url(#harbor-water)"/>
 <path d="M$sea $stepY H${sea + stepWidth}V${stepY + 23}H${sea + stepWidth * 2}V$height" fill="none" stroke="#304742" stroke-width="7"/>
 <path d="M$sea ${stepY - 2}H${sea + stepWidth + 2}V${stepY + 21}H${sea + stepWidth * 2 + 2}V$height" fill="none" stroke="#e8dfc5" stroke-width="3"/>
 <use href="#harbor-bollard" x="${sea + stepWidth + 6}" y="${stepY + 14}"/>
 </g>
""")
    append(drawHarborRoad(layout))
    if (!mobile) {
        val treeY = layout.headerHeight + layout.bodyHeight + 76
        append("""<use href="#harbor-tree" transform="translate(${end - 118} $treeY) scale(1.08)"/><use href="#harbor-tree" transform="translate(${end - 70} ${treeY + 11}) scale(.97)"/><use href="#harbor-palm" transform="translate(${end - 28} ${treeY - 10}) scale(.85)"/>""")
        append("""<use href="#harbor-pallet" x="${layout.projectStart + 185}" y="${treeY - 59}"/><use href="#harbor-pallet" x="${layout.projectStart + 204}" y="${treeY - 59}"/>""")
    }
    append("</g>")
}

private fun drawHarborRoad(layout: HarborLayout): String = buildString {
    val mobile = layout == HarborLayout.MOBILE
    val end = layout.quayEnd
    val railWidth = if (mobile) 7 else 20
    val roadX = end + railWidth + if (mobile) 3 else 10
    val roadWidth = if (mobile) 14 else 59
    val roadCenter = roadX + roadWidth / 2
    append("""
 <rect x="$end" width="${layout.width - end}" height="${layout.height}" fill="#d1cbbb"/>
 <rect x="${end + 2}" width="$railWidth" height="${layout.height}" fill="url(#harbor-rail)"/>
 <path d="M${end - 2} 0V${layout.height}M${roadX - 3} 0V${layout.height}" stroke="#f6ebc9" stroke-width="2"/>
 <rect x="$roadX" width="$roadWidth" height="${layout.height}" fill="#686f6c"/>
 <path d="M${roadX + 2} 0V${layout.height}M${roadX + roadWidth - 2} 0V${layout.height}" stroke="#efcf58" stroke-width="${if (mobile) .65 else 1.6}"/>
 <path d="M$roadCenter 0V${layout.height}" stroke="#fff3cd" stroke-width="${if (mobile) .7 else 1.7}" stroke-dasharray="${if (mobile) "11 19" else "32 24"}"/>
""")
    if (mobile) return@buildString
    val yard = roadX + roadWidth + 8
    append("""<rect x="$yard" width="${layout.width - yard}" height="${layout.height}" fill="#798969"/>""")
    for (row in 0 until layout.rows) {
        val y = layout.headerHeight + row * layout.rowHeight
        val roofX = yard + 12
        append("""
 <path d="M${roofX - 3} ${y + 91}H${layout.width}V${y + 232}H${roofX - 3}Z" fill="#a94c32" stroke="#46584d" stroke-width="2"/>
 <rect x="$roofX" y="${y + 87}" width="120" height="137" fill="#c7ccbf" stroke="#53655b" stroke-width="2"/>
 <rect x="${roofX + 4}" y="${y + 90}" width="116" height="131" fill="url(#harbor-cargo-ribs)"/>
 <path d="M${roofX + 9} ${y + 90}V${y + 218}" stroke="#eff0d7" stroke-width="2"/>
 <use href="#harbor-tree" transform="translate(${yard + 26} ${y + 41}) scale(.93)"/>
 <use href="#harbor-tree" transform="translate(${yard + 54} ${y + 14}) scale(.88)"/>
 <use href="#harbor-vehicle" x="${roadCenter - if (row % 2 == 0) 13 else -13}" y="${y + 152}" style="color:${if (row % 3 == 1) "#f7f5e9" else "#edbd25"}"/>
 <path d="M${roadCenter - 12} ${y + 53}V${y + 24}M${roadCenter - 17} ${y + 31}L${roadCenter - 12} ${y + 20}L${roadCenter - 7} ${y + 31}" fill="none" stroke="#f5edcd" stroke-width="2"/>
""")
    }
    append("""<use href="#harbor-tree" transform="translate(${yard + 23} 27) scale(.9)"/><use href="#harbor-tree" transform="translate(${yard + 27} 83) scale(.92)"/><use href="#harbor-palm" transform="translate(${yard + 27} ${layout.height - 26}) scale(.9)"/>""")
}

internal fun drawHarborCranes(layout: HarborLayout): String = buildString {
    val scale = when (layout) { HarborLayout.MOBILE -> .43; HarborLayout.DESKTOP -> .87; HarborLayout.README -> 1.02 }
    val x = layout.seaWidth + if (layout == HarborLayout.MOBILE) 7 else 16
    for (row in 0 until layout.rows) {
        val y = layout.headerHeight + row * layout.rowHeight + if (layout == HarborLayout.MOBILE) 49 else 82
        append("""<use aria-hidden="true" pointer-events="none" href="#harbor-crane" transform="translate($x $y) scale($scale)"/>""")
    }
}

internal fun drawProject(repository: RepositoryStats, index: Int, layout: HarborLayout): String {
    val mobile = layout == HarborLayout.MOBILE
    val row = index / layout.columns
    val column = index % layout.columns
    val width = layout.projectWidth
    val x = layout.projectStart + column * (width + layout.projectGap)
    val top = if (mobile) 26 else 40
    val y = layout.headerHeight + row * layout.rowHeight + top
    val height = layout.rowHeight - top * 2
    val textSize = if (mobile) 29 else if (layout == HarborLayout.README) 39 else 38
    val titleY = if (mobile) 51 else 65
    val statsY = if (mobile) 95 else 119
    val valueSize = if (mobile) 24 else 29
    val statsX = if (mobile) 30 else 55
    val secondX = width / 2 + if (mobile) 4 else 14
    val description = listOf("Multiplatform maps", "Compose timeline", "Kotlin compression", "Keystore management", "Kotlin logging", "WireGuard on the JVM")[index]
    val stars = repository.stars?.let(::compactCount) ?: "—"
    val starLabel = repository.stars?.let { "${formatNumber(it)} GitHub stars" } ?: "GitHub stars unavailable"
    val accessible = escapeXml("${repository.name}. $description. $starLabel. ${formatNumber(repository.views.total)} recorded repository views. Open on GitHub.")
    return """
<a class="project-link" href="https://github.com/rafambn/${repository.name}" aria-label="$accessible" tabindex="0" data-repository="${repository.name}" data-stars="${repository.stars ?: "unknown"}" data-views="${repository.views.total}" data-row="$row" data-column="$column" transform="translate($x $y)">
 <title>${escapeXml(repository.name)} · $starLabel · ${formatNumber(repository.views.total)} recorded views</title>
 <g aria-hidden="true">
  <rect x="-8" y="-8" width="${width + 16}" height="${height + 16}" fill="transparent"/>
  <rect class="focus-ring" x="-7" y="-7" width="${width + 14}" height="${height + 14}" rx="3" fill="none" stroke="#062c43" stroke-width="3"/>
  <g class="container-roof">
   <rect width="$width" height="$height" rx="2" fill="#fff7df" stroke="#585f50" stroke-width="1.4" filter="url(#harbor-shadow)"/>
   <rect x="4" y="4" width="${width - 8}" height="${height - 8}" fill="url(#harbor-roof-ribs)"/>
   <path d="M3 5H${width - 3}M3 ${height - 4}H${width - 3}" stroke="#fffdf0" stroke-width="2.1"/>
   <path d="M5 6V${height - 6}M${width - 5} 6V${height - 6}" stroke="#968c70" stroke-width="1"/>
  </g>
  <use href="#harbor-casting" x="1" y="1"/><use href="#harbor-casting" x="${width - 6}" y="1"/>
  <use href="#harbor-casting" x="1" y="${height - 6}"/><use href="#harbor-casting" x="${width - 6}" y="${height - 6}"/>
  <text class="project-name roof-lettering" x="${width / 2 - 11}" y="$titleY" text-anchor="middle" font-size="$textSize">${repository.name}</text>
  <g transform="translate(${width - if (mobile) 31 else 38} ${titleY - if (mobile) 20 else 26}) scale(${if (mobile) .83 else 1.06})"><use class="link-arrow" href="#harbor-arrow"/></g>
  <use href="#harbor-star" x="$statsX" y="${statsY - 17}"/>
  <text class="roof-lettering" x="${statsX + 28}" y="$statsY" font-size="$valueSize">$stars</text>
  <use href="#harbor-eye" x="$secondX" y="${statsY - 16}"/>
  <text class="roof-lettering" x="${secondX + 30}" y="$statsY" font-size="$valueSize">${compactCount(repository.views.total)}</text>
 </g>
</a>
"""
}
