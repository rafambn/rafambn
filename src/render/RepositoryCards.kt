package com.rafambn.profilebanner.render

import com.rafambn.profilebanner.RepositoryStats
import com.rafambn.profilebanner.pinnedRepos
import java.util.Base64
import java.util.Locale

internal fun renderRepositoryCards(center: Int, stats: Map<String, RepositoryStats>): String = buildString {
    append(repositoryCardDefinitions)
    pinnedRepos.forEachIndexed { index, repository ->
        val x = center + if (index % 2 == 0) -416 else 124
        val y = -770 + index / 2 * 250
        val counts = stats[repository] ?: RepositoryStats()
        val stars = counts.stars?.let(::formatCompactCount) ?: "—"
        val views = formatCompactCount(counts.views)
        val starDescription = counts.stars?.let { "${formatNumber(it)} GitHub stars" }
            ?: "GitHub stars temporarily unavailable"
        val description = when (repository) {
            "KMaP" -> listOf("Multiplatform maps")
            "FrameBar" -> listOf("Compose timeline", "and seekbar")
            "KFlate" -> listOf("Kotlin compression")
            "KeyManager" -> listOf("Keystore management")
            "Scribe" -> listOf("Kotlin logging")
            else -> listOf("WireGuard on the JVM")
        }
        val accessibleLabel = escapeXml("$repository, $starDescription, ${formatNumber(counts.views)} recorded views. Open repository on GitHub")
        append("""
<a class="repo-card" href="https://github.com/rafambn/$repository" tabindex="0" aria-label="$accessibleLabel" data-repository="$repository" data-stars="${counts.stars ?: "unknown"}" data-views="${counts.views}" transform="translate($x $y)">
<title>$repository · $starDescription · ${formatNumber(counts.views)} views recorded by the repository badge</title>
<g aria-hidden="true">
 <rect class="repo-focus" x="-6" y="-6" width="304" height="184" rx="8" fill="none" stroke="#163e57" stroke-width="3"/>
 <path d="M8 172V176H28V172M264 172V176H284V172" fill="#653b2a" stroke="#cb8b58" stroke-width="2"/>
 <path d="M282 0L298 13V170L292 177 280 170Z" fill="#673b2b" stroke="#4e3329"/>
 <path d="M0 7L7 0H280L292 12V164L284 172H7L0 165Z" fill="url(#repo-frame)" stroke="#613725" stroke-width="2"/>
 <path d="M7 1H279L291 12M1 7V163L8 170H283" fill="none" stroke="#e4a36a" stroke-width="1.5"/>
 <rect class="repo-panel" x="6" y="6" width="278" height="159" rx="3" fill="url(#repo-paper)" stroke="#896b4c" stroke-width="1.3"/>
 <path d="M12 7H279M7 12V157" stroke="#fff1d7" stroke-width="1.2"/>
 <use href="#repo-screw" x="12" y="13"/><use href="#repo-screw" x="278" y="13"/>
 <use href="#repo-screw" x="12" y="158"/><use href="#repo-screw" x="278" y="158"/>
 <use href="#repo-icon-${repository.lowercase(Locale.ROOT)}" x="22" y="29" width="43" height="43"/>
 <g class="repo-type" fill="#152f42">
  <text class="repo-name" x="77" y="46" font-size="27">$repository</text>
""")
        description.forEachIndexed { line, text ->
            append("""<text x="77" y="${70 + line * 20}" font-size="18">${escapeXml(text)}</text>""")
        }
        append("""
  <path class="repo-external" d="M264 25H272V33M271 26L260 37M267 36V41H255V29H260" fill="none" stroke="#264355" stroke-width="1.8" stroke-linecap="square"/>
  <path d="M22 105H268M147 118V150" fill="none" stroke="#b19878" stroke-width=".8"/>
  <use href="#repo-star" x="26" y="119"/>
  <text x="50" y="130" font-size="17">Stars</text>
  <text x="50" y="151" font-size="21">$stars</text>
  <use href="#repo-eye" x="164" y="120"/>
  <text x="192" y="130" font-size="17">Views</text>
  <text x="192" y="151" font-size="21">$views</text>
 </g>
</g>
</a>
""")
    }
}

private val repositoryCardFont = Base64.getEncoder().encodeToString(checkNotNull(
    object {}.javaClass.getResource("/fonts/BarlowCondensed-SemiBold.woff2")
).readBytes())

private val repositoryCardDefinitions = """
<style>
@font-face{font-family:RepositoryPanel;src:url(data:font/woff2;base64,$repositoryCardFont) format('woff2');font-weight:600;font-style:normal}
.repo-type{font-family:RepositoryPanel,'Arial Narrow',sans-serif;font-weight:600;font-variant-numeric:tabular-nums}
.repo-card{cursor:pointer;outline:none}
.repo-focus{opacity:0}
.repo-card:focus .repo-focus{opacity:1}
.repo-card:hover .repo-panel,.repo-card:focus .repo-panel{fill:#fff1d7}
.repo-card:hover .repo-name,.repo-card:focus .repo-name{text-decoration:underline;text-underline-offset:4px}
</style>
<defs>
 <linearGradient id="repo-paper" x1="0" y1="0" x2="1" y2=".8"><stop stop-color="#fff0d3"/><stop offset=".55" stop-color="#eed8b8"/><stop offset="1" stop-color="#ddc19d"/></linearGradient>
 <linearGradient id="repo-frame" x2="0" y2="1"><stop stop-color="#c28050"/><stop offset=".2" stop-color="#885030"/><stop offset=".8" stop-color="#693e2b"/><stop offset="1" stop-color="#b77646"/></linearGradient>
 <g id="repo-screw"><circle r="2.8" fill="#6b5d48"/><circle cy="-.4" r="1.9" fill="#ceb48a"/><path d="M-1.2.8L1.2-1.2" stroke="#665745" stroke-width=".8"/></g>
 <g id="repo-star"><path d="M8 0L10.5 5.4 16.5 6.1 12.1 10.2 13.3 16.1 8 13.2 2.7 16.1 3.9 10.2-.5 6.1 5.5 5.4Z"/></g>
 <g id="repo-eye"><path d="M0 7Q11-7 22 7Q11 21 0 7Z"/><circle cx="11" cy="7" r="4.7" fill="#eed8b8"/><circle cx="11" cy="7" r="2.4"/></g>
 <symbol id="repo-icon-kmap" viewBox="0 0 48 48">
  <path d="M3 9L17 3 31 10 45 3V39L31 45 17 38 3 45Z" fill="#397c89"/>
  <path d="M3 9L17 3V38L3 45Z" fill="#70a293"/><path d="M31 10L45 3V39L31 45Z" fill="#7da354"/>
  <path d="M4 29L12 21 17 25 26 16 31 23 44 14M17 3V38M31 10V45" fill="none" stroke="#b1bf72" stroke-width="2"/>
 </symbol>
 <symbol id="repo-icon-framebar" viewBox="0 0 48 48">
  <rect x="3" y="6" width="42" height="36" rx="3" fill="#17374b"/><path d="M13 10H35V38H13Z" fill="#f3dfbe"/>
  <path d="M20 17L30 24 20 31Z" fill="#17374b"/>
  <path d="M7 11H9M7 18H9M7 25H9M7 32H9M7 38H9M39 11H41M39 18H41M39 25H41M39 32H41M39 38H41" stroke="#f3dfbe" stroke-width="3"/>
 </symbol>
 <symbol id="repo-icon-kflate" viewBox="0 0 48 48" fill="#17374b">
  <path d="M7 11V36C7 46 41 46 41 36V11Z"/><ellipse cx="24" cy="10" rx="17" ry="7"/>
  <path d="M7 11C7 21 41 21 41 11M7 23C7 33 41 33 41 23M7 32C7 42 41 42 41 32" fill="none" stroke="#f0dcbb" stroke-width="2"/>
 </symbol>
 <symbol id="repo-icon-keymanager" viewBox="0 0 48 48">
  <path d="M22 27L5 44H0V34L17 17A14 14 0 1 1 22 27Z" fill="#17374b"/>
  <circle cx="32" cy="11" r="4" fill="#f4dfbe"/><path d="M7 32L14 39M12 27L19 34" stroke="#f0dcbb" stroke-width="2"/>
 </symbol>
 <symbol id="repo-icon-scribe" viewBox="0 0 48 48" fill="none" stroke="#17374b" stroke-width="2.5" stroke-linejoin="round">
  <path d="M6 5H28L36 13V44H6Z"/><path d="M28 5V14H36M11 24H24M11 31H21M11 38H18"/>
  <path d="M19 38L27 19Q32 6 45 3Q46 18 31 28L19 38Z" fill="#376e68" stroke="#244f50"/>
  <path d="M17 43L38 12" stroke="#d1ab69" stroke-width="2"/>
 </symbol>
 <symbol id="repo-icon-wg-kotlin" viewBox="0 0 48 48" fill="#eed8b8" stroke="#17374b" stroke-width="4">
  <path d="M24 8L7 38H41Z" fill="none"/><circle cx="24" cy="8" r="6"/><circle cx="7" cy="38" r="6"/><circle cx="41" cy="38" r="6"/>
 </symbol>
</defs>
"""
