package com.rafambn.profilebanner.render

internal fun drawCargoShip(layout: HarborLayout): String = buildString {
    val mobile = layout == HarborLayout.MOBILE
    val width = when (layout) { HarborLayout.DESKTOP -> 184; HarborLayout.MOBILE -> 60; HarborLayout.README -> 180 }
    val height = layout.bodyHeight - if (mobile) 120 else 100
    val x = (layout.seaWidth - width) / 2 - 5
    val y = layout.headerHeight + (layout.bodyHeight - height) / 2
    val center = width / 2
    val bow = if (mobile) 58 else 119
    val stern = if (mobile) 47 else 98
    val hull = "M$center 0C${width - 8} 33 $width ${bow - 23} $width $bow V${height - stern}Q$width ${height - 36} $center $height Q0 ${height - 36} 0 ${height - stern}V$bow C0 ${bow - 23} 8 33 $center 0Z"
    append("""
<g aria-hidden="true" data-art="cargo-ship" transform="translate($x $y)">
 <path d="$hull" fill="#c8482e" stroke="#573e32" stroke-width="1.8" filter="url(#harbor-shadow)"/>
 <path d="M$center 8C${width - 15} 37 ${width - 7} ${bow - 13} ${width - 7} $bow V${height - stern}Q${width - 7} ${height - 38} $center ${height - 10}Q7 ${height - 38} 7 ${height - stern}V$bow C7 ${bow - 13} 15 37 $center 8Z" fill="#388b70" stroke="#183f3d" stroke-width="1.2"/>
 <path d="M$center 3C${width - 11} 34 ${width - 3} ${bow - 23} ${width - 3} $bow V${height - stern}Q${width - 3} ${height - 36} $center ${height - 4}Q3 ${height - 36} 3 ${height - stern}V$bow C3 ${bow - 23} 11 34 $center 3Z" fill="none" stroke="#f9eac3" stroke-width="1.1"/>
 <path d="M$center 4C${width - 12} 36 ${width - 4} ${bow - 22} ${width - 4} $bow V${height - stern}Q${width - 4} ${height - 36} $center ${height - 5}Q4 ${height - 36} 4 ${height - stern}V$bow C4 ${bow - 22} 12 36 $center 4Z" fill="none" stroke="#233f3f" stroke-width="2" stroke-dasharray="1 10"/>
 <path d="M$center 20V${bow - 27}" stroke="#173f3c" stroke-width="${if (mobile) 5 else 8}"/>
 <path d="M$center 20V${bow - 27}" stroke="#f5c93e" stroke-width="${if (mobile) 3 else 5}"/>
 <circle cx="$center" cy="${if (mobile) 18 else 28}" r="${if (mobile) 3 else 6}" fill="#f8d54c" stroke="#625e36"/>
 <circle cx="$center" cy="${if (mobile) 40 else 70}" r="${if (mobile) 4 else 8}" fill="#f3cd44" stroke="#435d45"/>
 <circle cx="${center - if (mobile) 12 else 36}" cy="${if (mobile) 53 else 75}" r="${if (mobile) 2.5 else 5}" fill="#eff1dc" stroke="#46665c"/>
 <circle cx="${center + if (mobile) 12 else 36}" cy="${if (mobile) 53 else 75}" r="${if (mobile) 2.5 else 5}" fill="#eff1dc" stroke="#46665c"/>
""")
    val bridgeX = if (mobile) 13 else 52
    val bridgeY = if (mobile) 66 else 99
    val bridgeWidth = width - bridgeX * 2
    val bridgeHeight = if (mobile) 41 else 77
    append("""
 <path d="M${bridgeX - 8} ${bridgeY + 8}H${width - bridgeX + 8}V${bridgeY + bridgeHeight + 6}H${bridgeX - 8}Z" fill="none" stroke="#b5d6bc" stroke-width="1"/>
 <rect x="${bridgeX - 10}" y="${bridgeY + 23}" width="${bridgeWidth + 20}" height="${if (mobile) 10 else 17}" fill="#e2e5d0" stroke="#4b7664"/>
 <rect x="$bridgeX" y="$bridgeY" width="$bridgeWidth" height="$bridgeHeight" fill="#f7f4df" stroke="#365b50" stroke-width="1.5" filter="url(#harbor-shadow)"/>
 <path d="M${bridgeX + 2} ${bridgeY + 2}H${width - bridgeX - 2}V${bridgeY + 8}H${bridgeX + 2}Z" fill="#e4e5d4"/>
 <rect x="${center - if (mobile) 4 else 10}" y="${bridgeY + if (mobile) 16 else 28}" width="${if (mobile) 8 else 20}" height="${if (mobile) 10 else 24}" fill="#20495d" stroke="#63878c" stroke-width="2"/>
 <path d="M${center - if (mobile) 3 else 8} ${bridgeY + if (mobile) 16 else 28}H${center + if (mobile) 3 else 8}" stroke="#d6ece0" stroke-width="1"/>
""")
    if (!mobile) {
        for (offset in listOf(11, 35, 60)) {
            append("""<rect x="${bridgeX - 13}" y="${bridgeY + offset}" width="8" height="10" fill="#e7f0d8" stroke="#688572"/><rect x="${width - bridgeX + 5}" y="${bridgeY + offset}" width="8" height="10" fill="#e7f0d8" stroke="#688572"/>""")
        }
    }
    val cargoColumns = if (mobile) 2 else 4
    val cargoRows = when (layout) { HarborLayout.DESKTOP -> 6; HarborLayout.MOBILE -> 12; HarborLayout.README -> 11 }
    val cargoStart = bridgeY + bridgeHeight + if (mobile) 12 else 20
    val cargoEnd = height - stern - 6
    val cargoWidth = (width - if (mobile) 17 else 28).toDouble() / cargoColumns
    val cargoHeight = (cargoEnd - cargoStart).toDouble() / cargoRows
    val colors = listOf("#d9512d", "#147fae", "#ed6f39", "#f2e7c4", "#176ca0")
    for (row in 0 until cargoRows) {
        for (column in 0 until cargoColumns) {
            val cargoX = (if (mobile) 8 else 13) + column * cargoWidth
            val cargoY = cargoStart + row * cargoHeight
            val color = colors[(row * 3 + column + if (row % 3 == 2) 1 else 0) % colors.size]
            append("""<use href="#harbor-cargo" x="$cargoX" y="$cargoY" width="${cargoWidth - 1.4}" height="${cargoHeight - 3}" style="color:$color"/>""")
        }
    }
    val sternY = cargoEnd + if (mobile) 11 else 20
    append("""
 <path d="M11 ${cargoEnd + 6}H${width - 11}M$center ${sternY + 5}V${height - 21}" stroke="#efedd2" stroke-width="1.4"/>
 <path d="M$center ${sternY + 7}V${height - 29}" stroke="#183f3c" stroke-width="${if (mobile) 5 else 8}"/>
 <path d="M$center ${sternY + 7}V${height - 29}" stroke="#f4c836" stroke-width="${if (mobile) 3 else 5}"/>
 <circle cx="$center" cy="${height - 25}" r="${if (mobile) 3 else 7}" fill="#f4cc3f" stroke="#455941"/>
 <circle cx="$center" cy="${height - 13}" r="${if (mobile) 2 else 4}" fill="#eaf0d9"/>
""")
    for (side in listOf(-1, 1)) {
        val fittingX = center + side * if (mobile) 12 else 38
        append("""<rect x="${fittingX - 3}" y="$sternY" width="6" height="9" rx="1" fill="#e7e6d0" stroke="#496554"/><circle cx="$fittingX" cy="${sternY + if (mobile) 15 else 25}" r="${if (mobile) 2 else 5}" fill="#ecf0da" stroke="#436e5b"/>""")
    }
    append("</g>")
    for (row in 0..layout.rows) {
        val dockY = layout.headerHeight + row * layout.rowHeight + 10
        val shipY = (dockY + if (row == 0) bow - 12 else if (row == layout.rows) -stern + 10 else 13)
            .coerceIn(y + bow - 12, y + height - stern + 10)
        val fromX = x + width - if (row == 0 || row == layout.rows) 8 else 2
        append("""<path aria-hidden="true" pointer-events="none" d="M$fromX $shipY L${layout.seaWidth + 8} $dockY" fill="none" stroke="#f7ce36" stroke-width="${if (mobile) 1 else 1.6}"/><use aria-hidden="true" href="#harbor-bollard" x="${layout.seaWidth + 8}" y="$dockY"/>""")
    }
}
