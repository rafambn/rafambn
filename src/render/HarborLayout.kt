package com.rafambn.profilebanner.render

enum class HarborLayout(
    val width: Int,
    val headerHeight: Int,
    val seaWidth: Int,
    val columns: Int,
    val rowHeight: Int,
    val footerHeight: Int
) {
    DESKTOP(1200, 184, 280, 2, 260, 100),
    MOBILE(360, 204, 82, 1, 184, 76),
    README(840, 216, 270, 1, 236, 108);

    val rows get() = 6 / columns
    val bodyHeight get() = rows * rowHeight
    val height get() = headerHeight + bodyHeight + footerHeight
    val quayEnd get() = width - when (this) { DESKTOP -> 144; MOBILE -> 24; README -> 136 }
    val projectStart get() = seaWidth + when (this) { DESKTOP -> 76; MOBILE -> 34; README -> 90 }
    val projectEnd get() = quayEnd - if (this == MOBILE) 8 else 36
    val projectGap get() = if (columns == 2) 42 else 0
    val projectWidth get() = (projectEnd - projectStart - projectGap) / columns
}
