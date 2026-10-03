package com.rafambn.profilebanner.render

fun renderRepositoryBadge(repository: String, totalViews: Long): String {
    val count = formatNumber(totalViews)
    val width = 92 + count.length * 9
    return """<svg xmlns="http://www.w3.org/2000/svg" width="$width" height="32" viewBox="0 0 $width 32" role="img" aria-label="${escapeXml(repository)}: $count recorded views"><rect width="$width" height="32" rx="3" fill="#153f46"/><path d="M6 5V27M10 5V27" stroke="#6b9693"/><text x="20" y="21" font-family="sans-serif" font-size="13" fill="#f4f0e5">views</text><text x="70" y="21" font-family="monospace" font-size="14" fill="#f4f0e5">$count</text></svg>"""
}
