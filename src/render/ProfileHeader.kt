package com.rafambn.profilebanner.render

import com.rafambn.profilebanner.counter.ViewStats
import java.util.Base64

internal fun renderProfileHeader(center: Int, stats: ViewStats, mobile: Boolean): String = buildString {
    append(profileHeaderDefinitions)
    append("""
<g id="profile-header" transform="translate($center -815) scale(${if (mobile) .84 else 1.0})">
 <text class="profile-name" y="-245" text-anchor="middle" role="heading" aria-level="1">Rafael Mendonça</text>
 <text class="profile-bio" y="-199" text-anchor="middle">Solutions architect. Making complex things simple.</text>
 <g id="profile-views" transform="translate(0 -161)" role="img" aria-label="Profile views: ${formatNumber(stats.today)} today, ${formatNumber(stats.week)} in the last 7 days, ${formatNumber(stats.month)} in the last 30 days, ${formatNumber(stats.total)} total" data-today="${stats.today}" data-week="${stats.week}" data-month="${stats.month}" data-total="${stats.total}">
  <title>Profile views · Today in UTC · Week: last 7 days · Month: last 30 days · Total: all time</title>
  <g aria-hidden="true">
   <path d="M-278 146V161H-247V146M247 146V161H278V146" fill="#583b2e" stroke="#c48756" stroke-width="2"/>
   <path d="M-269 150V159M256 150V159" stroke="#e0a674" stroke-width="3"/>
   <path d="M-302 0V-12H-280V0M-295-12V-19M282 0V-10H302V0M292-10V-20" fill="none" stroke="#71503c" stroke-width="3"/>
   <path d="M-302-11H-280M282-9H302" stroke="#d8aa79" stroke-width="1.3"/>
   <path d="M319 0L336 12V144L327 157H-316L-329 147Z" fill="#543d34" stroke="#49372e" stroke-width="2"/>
   <path d="M-330 10L-320 0H319L330 11V139L319 150H-319L-330 139Z" fill="url(#profile-steel)" stroke="#684631" stroke-width="2"/>
   <path d="M-329 11L-319 1H318L329 12M-329 12V138L-318 149H318" fill="none" stroke="#dcac79" stroke-width="1.8"/>
   <path d="M-316 3H-249L-266 16H-318ZM254 2H317L327 12V61L315 48V17H270Z" fill="#a56840" opacity=".55"/>
   <path d="M-327 83L-319 77V123L-325 134M309 131L302 144H270" fill="none" stroke="#9b5835" stroke-width="3" opacity=".7"/>
   <rect x="-310" y="18" width="620" height="116" rx="5" fill="#433a32" stroke="#c39970" stroke-width="1.5"/>
   <rect x="-304" y="23" width="608" height="105" rx="3" fill="url(#profile-display)" stroke="#172633" stroke-width="2"/>
   <path d="M-302 126H302M303 26V125" fill="none" stroke="#899292" stroke-width="1" opacity=".8"/>
   <path d="M-300 25H301" stroke="#0a1823" stroke-width="3" opacity=".65"/>
   <path d="M-152 42V109M0 42V109M152 42V109" stroke="#a1a8a6" stroke-width="1.5" opacity=".8"/>
   <path d="M-152 43V109M0 43V109M152 43V109" transform="translate(1 0)" stroke="#172630" stroke-width="1"/>
""")
    for ((label, count) in listOf("Today" to stats.today, "Week" to stats.week, "Month" to stats.month, "Total" to stats.total)) {
        val x = when (label) {
            "Today" -> -228
            "Week" -> -76
            "Month" -> 76
            else -> 228
        }
        append("""
   <text class="profile-stat-label" x="$x" y="54" text-anchor="middle">$label</text>
   <text class="profile-stat-value" x="$x" y="103" text-anchor="middle">${formatCompactCount(count)}</text>
""")
    }
    for ((x, y) in listOf(-318 to 12, 0 to 9, 318 to 13, -319 to 76, 319 to 76, -318 to 138, 318 to 138)) {
        append("""<use href="#profile-fastener" x="$x" y="$y"/>""")
    }
    append("""
   <path d="M-145 143H-108M-91 139H-77M102 142H126M173 7H199M-325 40V54" stroke="#d5b18a" stroke-width="1" opacity=".38"/>
   <rect x="-24" y="139" width="48" height="6" rx="2" fill="#c6a477" stroke="#5b4838"/>
   <circle cx="-18" cy="142" r="1.5" fill="#3c3a32"/><circle cx="18" cy="142" r="1.5" fill="#3c3a32"/>
   <path d="M-10 142H10" stroke="#f2d4a2" stroke-width="1.5"/>
  </g>
 </g>
</g>
""")
}

private val profileHeaderDefinitions = """
<style>
@font-face{font-family:ProfileName;src:url(data:font/woff2;base64,${Base64.getEncoder().encodeToString(checkNotNull(object {}.javaClass.getResource("/fonts/SourceSerif4-Bold.woff2")).readBytes())}) format('woff2');font-weight:700;font-style:normal}
@font-face{font-family:ProfileBody;src:url(data:font/woff2;base64,${Base64.getEncoder().encodeToString(checkNotNull(object {}.javaClass.getResource("/fonts/Barlow-Regular.woff2")).readBytes())}) format('woff2');font-weight:400;font-style:normal}
.profile-name{font-family:ProfileName,serif;font-size:90px;font-weight:700;letter-spacing:-2px;fill:#102536}
.profile-bio{font-family:ProfileBody,sans-serif;font-size:31px;fill:#172e40}
.profile-stat-label{font-family:ProfileBody,sans-serif;font-size:22px;letter-spacing:1px;fill:#e6e2d7}
.profile-stat-value{font-family:RepositoryPanel,sans-serif;font-size:42px;font-weight:600;font-variant-numeric:tabular-nums;fill:#fff4df}
</style>
<defs>
 <linearGradient id="profile-steel" x1="0" y1="0" x2=".15" y2="1"><stop stop-color="#ac825c"/><stop offset=".12" stop-color="#776957"/><stop offset=".5" stop-color="#625b50"/><stop offset=".87" stop-color="#837968"/><stop offset="1" stop-color="#b49a76"/></linearGradient>
 <linearGradient id="profile-display" x1="0" y1="0" x2="1" y2="1"><stop stop-color="#15232f"/><stop offset=".55" stop-color="#263744"/><stop offset="1" stop-color="#1c2b38"/></linearGradient>
 <g id="profile-fastener"><circle r="3.1" fill="#4c3b2d"/><path d="M-2-1L0-2.3 2-1V1L0 2.3-2 1Z" fill="#b77c4c"/><path d="M-1.4-1.3H1M-.9.9L.9-.9" stroke="#e0b17a" stroke-width=".8"/></g>
</defs>
"""
