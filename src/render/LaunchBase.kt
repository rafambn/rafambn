package com.rafambn.profilebanner.render

fun renderLaunchBaseSvg(mobile: Boolean = false): String = buildString {
    val width = if (mobile) 720 else 1600
    val center = width / 2
    val glassWidth = if (mobile) 410 else 660
    val left = center - glassWidth / 2
    val right = center + glassWidth / 2
    val upperFloor = 215
    val lowerFloor = 259
    val bottom = 370
    val stairs = listOf(left + 129, right - 129)
    val upperRailings = listOf(0 to stairs[0] - 31, stairs[0] + 31 to stairs[1] - 31, stairs[1] + 31 to width)
    val plantingBeds = listOf(left to stairs[0] - 31, stairs[0] + 31 to stairs[1] - 31, stairs[1] + 31 to right)
    append("""<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="$width" height="670" viewBox="0 -300 $width 670" style="display:block;width:100%;height:auto" role="img" aria-labelledby="title desc">
<title id="title">Mission Control — launch base</title>
<desc id="desc">A launch platform overlooking a coastal bay at dawn, with golden clouds, misty mountains, a continuous concrete foundation, amber lamps and trees at its edges. Painted on the wall: Great things begin with a small step</desc>
<style>
@keyframes beacon {0%,100%{opacity:1} 50%{opacity:.12}}
.lamp-light {animation:beacon 1.6s ease-in-out infinite}
@media (prefers-reduced-motion:reduce){.lamp-light{animation:none}}
</style>
<defs>
 <linearGradient id="concrete" x2=".85" y2="1"><stop stop-color="#e1cfb4"/><stop offset=".4" stop-color="#b4b0a5"/><stop offset="1" stop-color="#7b8584"/></linearGradient>
 <radialGradient id="glow"><stop stop-color="#fff4b3" stop-opacity=".9"/><stop offset=".25" stop-color="#ffd578" stop-opacity=".35"/><stop offset="1" stop-color="#ffae50" stop-opacity="0"/></radialGradient>
 <pattern id="panel-seams" width="84" height="70" patternUnits="userSpaceOnUse"><path d="M84 0H0V70" fill="none" stroke="#5d6a68" stroke-opacity=".18"/><circle cx="6" cy="6" r="1" fill="#67716c" opacity=".5"/></pattern>
 <g id="lamp"><path d="M0 0V-41" stroke="#485854" stroke-width="4"/><path d="M-4-39H4V-52H-4Z" fill="#ffd18a" stroke="#a87d47" stroke-width="2"/><g class="lamp-light"><circle cy="-46" r="25" fill="url(#glow)"/><rect x="-2.5" y="-50" width="5" height="9" rx="1" fill="#fff4bf"/></g></g>
 <pattern id="aggregate" width="97" height="71" patternUnits="userSpaceOnUse">
 <path d="M3 8h3m13 14h2m21-17h4m18 24h2m24-13h3M9 49h4m22 13h2m16-18h3m22 17h5m10-20h2" stroke="#4e5950" stroke-width="1" opacity=".26"/>
 <path d="M12 17h4m18 20h5m23-20h3m18 34h4M5 66h5m42-7h4m32 9h3" stroke="#fff6da" opacity=".48"/>
 <circle cx="24" cy="46" r="1.2" fill="#5c6558" opacity=".2"/><circle cx="68" cy="8" r=".8" fill="#5c6558" opacity=".35"/>
 </pattern>
 <pattern id="paint-wear" width="73" height="41" patternUnits="userSpaceOnUse">
 <rect width="73" height="41" fill="white"/>
 <path d="M5 8l2-.5 1.1.7-2.3.9ZM28 4l.8 1.8-1.2.5-.4-1.6ZM47 15l3.1-.7.5.8-2.9.9ZM13 27l1.6-.7 1 1.4-1.8.5ZM59 33l2.8-.4-.6 1-2.3.3ZM34 36l.8-1.4.9.6-.6 1.6Z" fill="#888"/>
 <path d="M21 17l1.7-.3-.4 1.2-1.3.2ZM64 7l1.1-.3.3 1.1-1 .5ZM41 24l2-.5.2.7-1.7.6Z" fill="#333"/>
 <path d="M8 37l6-.8M51 4l3-.5M36 12l4-.7" fill="none" stroke="#bbb" stroke-width=".45"/>
 </pattern>
 <mask id="motto-paint" maskUnits="userSpaceOnUse" x="-220" y="-30" width="440" height="70"><rect x="-220" y="-30" width="440" height="70" fill="url(#paint-wear)"/></mask>
 $wallLetteringDefinitions
</defs>
""")
    append(coastalDawnBackground)
    for ((start, end) in upperRailings) {
        append(launchRailing(start, end, upperFloor))
    }
    for (x in 155 until width - 100 step 215) {
        if (stairs.none { kotlin.math.abs(it - x) < 38 }) {
            append("""<use href="#lamp" transform="translate($x $upperFloor)"/>""")
        }
    }
    // The concrete covers the last unit of each support to keep floor joins closed at any scale.
    append("""<rect y="$upperFloor" width="$width" height="${bottom - upperFloor}" fill="url(#concrete)"/>
<rect y="$upperFloor" width="$width" height="${bottom - upperFloor}" fill="url(#aggregate)"/>
<rect y="$upperFloor" width="$width" height="${bottom - upperFloor}" fill="url(#panel-seams)"/>
<rect y="$upperFloor" width="$width" height="3" fill="#e5d3ae"/>
""")
    for ((start, end) in plantingBeds) {
        // At this scale, foliage extends 7 units left and 62 right of its origin.
        val first = start + 8 + 7
        val last = end - 8 - 62
        val count = maxOf(1, (end - start - 16) / 53)
        for (index in 0 until count) {
            if (start == stairs[0] + 31 && index == 0) continue
            val x = if (count == 1) (first + last) / 2 else first + (last - first) * index / (count - 1)
            append(launchShrub(x, lowerFloor + 1, .43, (x / 53) % 2 == 0))
        }
    }
    for (x in stairs) {
        append("""<g transform="translate($x 0)">
<rect x="-29" y="$upperFloor" width="58" height="${lowerFloor - upperFloor + 1}" fill="#8e7136"/>
""")
        for (step in 0 until 6) {
            val y = upperFloor + (lowerFloor - upperFloor) * step / 6
            append("""<rect x="-29" y="$y" width="58" height="4" fill="#e3b64b"/><path d="M-29 $y H29" stroke="#ffe08a" stroke-width="1.4"/>""")
        }
        append("""<path d="M-31 ${lowerFloor + 1}V${upperFloor - 28}M31 ${lowerFloor + 1}V${upperFloor - 28}" stroke="#9b7d38" stroke-width="5"/>
<path d="M-32 ${lowerFloor + 1}V${upperFloor - 28}M30 ${lowerFloor + 1}V${upperFloor - 28}" stroke="#f3ce69" stroke-width="2"/>
</g>""")
    }
    val overlappingShrub = stairs[0] + 27
    append(launchShrub(overlappingShrub, lowerFloor + 1, .43, (overlappingShrub / 53) % 2 == 0))
    append(launchRailing(left + 2, right - 2, lowerFloor))
    append("""<rect x="$left" y="$lowerFloor" width="$glassWidth" height="${bottom - lowerFloor}" fill="url(#concrete)"/>
<rect x="$left" y="$lowerFloor" width="$glassWidth" height="${bottom - lowerFloor}" fill="url(#aggregate)"/>
<rect x="$left" y="$lowerFloor" width="$glassWidth" height="${bottom - lowerFloor}" fill="url(#panel-seams)"/>
<rect x="${left - 1}" y="$lowerFloor" width="${glassWidth + 2}" height="3" fill="#e5d3ae"/>
<path d="M$left $lowerFloor V$bottom M$right $lowerFloor V$bottom" fill="none" stroke="#68746b" stroke-width="2" opacity=".6"/>
<g transform="translate($center ${if (mobile) 319 else 315}) scale(${if (mobile) .8 else 1.0})" fill="#3b4841" opacity=".9" mask="url(#motto-paint)"><use href="#launch-motto"/></g>
""")
    for (x in listOf(left + 2, right - 2)) {
        append("""<use href="#lamp" transform="translate($x $lowerFloor)"/>""")
    }
    append(launchVegetation(width))
    append("</svg>")
}

private val coastalDawnBackground = checkNotNull(
    object {}.javaClass.getResource("/backgrounds/coastal-dawn.svg")
).readText()

private val wallLetteringDefinitions = checkNotNull(
    object {}.javaClass.getResource("/lettering/launch-motto.svg")
).readText()

private val foregroundTreeDefinitions = checkNotNull(
    object {}.javaClass.getResource("/trees/foreground.svg")
).readText()

private fun launchVegetation(width: Int): String = """
$foregroundTreeDefinitions
<g aria-label="Close foreground oak trees, with original branching and colored foliage">
<use href="#arch-oak" x="-258" y="-295" width="665" height="760"/>
<g transform="translate($width 0) scale(-1 1)">
<use href="#forked-tree" x="-253" y="-235" width="560" height="732"/>
</g>
<use href="#forked-tree" x="-106" y="-57" width="357" height="467"/>
<use href="#arch-oak" x="-76" y="9" width="360" height="411"/>
<use href="#arch-oak" x="${width - 233}" y="-19" width="380" height="434"/>
<use href="#forked-tree" x="${width - 205}" y="26" width="305" height="399"/>
</g>
"""

private fun launchShrub(x: Int, y: Int, scale: Double, flipped: Boolean): String = buildString {
    append("""<g transform="translate($x $y) scale($scale)"><g transform="${if (flipped) "translate(130 0) scale(-1 1)" else "translate(0 0)"}">
<path d="M-10 0L-15-24L-2-20L-12-45L3-38L0-61L15-50L20-79L33-63L45-88L51-71L73-93L71-70L91-78L85-57L109-69L103-46L127-48L117-31L142-28L131-12L144 0Z" fill="#284e3c"/>
<path d="M-3-42L12-36L10-56L25-45L27-68L39-54L51-78L56-61L73-78L68-56L91-65L82-43L104-53L95-31L119-36L111-17L76-9L33-19Z" fill="#507947"/>
<path d="M10-56L25-45L27-68L39-54L51-78L56-61L73-78L66-58L55-49L42-57L32-40L21-43Z" fill="#91a55b"/>
<path d="M30 0Q35-35 53-65M37-23L13-48M42-39L72-61M70 0Q78-32 100-44M78-22L62-41" fill="none" stroke="#b4a56b" stroke-width="1.7"/>
<path d="M22 0Q5-27-14-32Q5-37 27-13Q18-55 30-73Q43-51 36-16Q52-54 70-52Q67-28 42-5Q79-25 99-16Q76 1 51 0Z" fill="#356d4d"/>
<path d="M30-73Q36-41 32-3M70-52Q48-26 38-3M-14-32Q10-24 29-3" fill="none" stroke="#8cae69" stroke-width="1.5"/>
<path d="M80 0Q78-33 94-61Q105-34 88-6Q112-39 133-34Q117-11 99 0Z" fill="#749447"/>
<path d="M94-61Q93-28 84-3M133-34Q108-21 95-3" fill="none" stroke="#c0c47a" stroke-width="1.3"/>
</g></g>""")
}

private fun launchRailing(left: Int, right: Int, y: Int): String = buildString {
    append("""<path d="M$left ${y - 25}H${right}M$left ${y - 10}H$right" stroke="#b79b66" stroke-width="3" fill="none"/>""")
    val sections = maxOf(1, (right - left + 42) / 43)
    for (index in 0..sections) {
        val x = left + (right - left) * index / sections
        append("""<path d="M$x ${y - 28}V${y + 1}" stroke="#786f51" stroke-width="3"/><path d="M${x + 1} ${y - 28}V${y + 1}" stroke="#dbbd80"/>""")
    }
}
