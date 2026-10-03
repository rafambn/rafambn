package com.rafambn.profilebanner.render

import java.util.Base64

private fun harborFont(family: String, file: String, weight: Int): String {
    val bytes = checkNotNull(object {}.javaClass.getResource("/fonts/$file")).readBytes()
    val encoded = Base64.getEncoder().encodeToString(bytes)
    return "@font-face{font-family:$family;src:url(data:font/woff2;base64,$encoded) format('woff2');font-weight:$weight;font-style:normal}"
}

internal val harborHeadingFonts = "<style>" +
    harborFont("HarborSerif", "SourceSerif4-Bold.woff2", 700) +
    harborFont("HarborSans", "Barlow-Regular.woff2", 400) + "</style>"

private fun drawWaterPattern(): String = buildString {
    val colors = listOf("#06739b", "#088bad", "#077ca3", "#0985aa", "#056f99")
    append("""
<g id="harbor-caustic-a">
 <path d="M-28-9Q-22-25-11-19T7-23Q23-23 26-7T18 15Q6 13-1 24T-23 10Q-19 0-28-9Z" fill="currentColor" opacity=".68"/>
 <path d="M-29-8C-19-8-18-2-10-3C-4-4-2-2 0-5C4-10 2-20 10-28C6-18 8-11 3-5C1-2 3 2 6 3C14 5 15 16 26 18C14 20 11 9 4 7C-3 5-1-1-9-1C-18 0-18-6-29-8Z" fill="#2caec7" opacity=".78"/>
 <path d="M5 7Q2 14-7 17T-13 26M-18-18Q-10-13-12-8M18-17Q14-9 22-6" fill="none" stroke="#37b6ca" stroke-width=".8" stroke-linecap="round" opacity=".64"/>
 <path d="M-4 1Q2 4 3 8M4-13Q2-6 3-3" fill="none" stroke="#74d0d9" stroke-width=".65" opacity=".7"/>
</g>
<g id="harbor-caustic-b">
 <path d="M-27-15Q-17-13-9-23T12-17Q14-5 27-5T22 14Q12 11 6 24T-9 15Q-25 24-23 8T-27-15Z" fill="currentColor" opacity=".72"/>
 <path d="M-30 4C-22 8-15 1-9 4C-2 7 1 3 3-3C6-8 15-9 18-17C18-8 10-6 7-1C3 4 5 9 9 12C15 15 12 21 18 29C10 25 12 18 5 14C0 10 4 4-8 7C-17 3-20 11-30 4Z" fill="#27a9c3" opacity=".82"/>
 <path d="M-7 5Q-14-2-9-10T-13-24M7 13Q19 6 26 10M-22 19Q-15 22-10 18" fill="none" stroke="#45bfd0" stroke-width="1" stroke-linecap="round" opacity=".64"/>
 <path d="M1 7Q4 3 5-1M-13 5-9 6" fill="none" stroke="#90d9df" stroke-width=".7" opacity=".6"/>
</g>
<g id="harbor-caustics">
""")
    for (row in 0..4) {
        for (column in 0..4) {
            val seed = row * 73 + column * 137 + row * column * 47
            val x = column * 32 + 16 + seed % 21 - 10
            val y = row * 32 + 16 + seed / 7 % 23 - 11
            val scale = (65 + seed % 55) / 100.0
            val color = colors[seed % colors.size]
            val shape = if (seed % 3 == 0) "a" else "b"
            append("""<use href="#harbor-caustic-$shape" transform="translate($x $y) rotate(${seed % 360}) scale($scale)" color="$color"/>""")
        }
    }
    append("""</g><pattern id="harbor-water" width="160" height="160" patternUnits="userSpaceOnUse"><rect width="160" height="160" fill="#087fa5"/>""")
    // Reuse the complete cluster at the tile edges, preserving one global water origin.
    for (x in listOf(-160, 0, 160)) {
        for (y in listOf(-160, 0, 160)) {
            append("""<use href="#harbor-caustics" x="$x" y="$y"/>""")
        }
    }
    append("</pattern>")
}

internal val harborDefinitions = "<style>" +
    harborFont("Harbor", "BarlowCondensed-SemiBold.woff2", 600) + """
.harbor-scene{font-family:Harbor,sans-serif;font-weight:600;color:#062c43;font-variant-numeric:tabular-nums}
.harbor-scene text{fill:#062c43}
.harbor-scene .profile-name{font-family:HarborSerif,serif;font-weight:700}
.harbor-scene .profile-sentence{font-family:HarborSans,sans-serif;font-weight:400}
.harbor-scene a{cursor:pointer;outline:none}
.harbor-scene .focus-ring{opacity:0}
.harbor-scene a:focus-visible .focus-ring{opacity:1}
.harbor-scene a:hover .container-roof,.harbor-scene a:focus-visible .container-roof{filter:brightness(1.045)}
.harbor-scene a:hover .project-name,.harbor-scene a:focus-visible .project-name{text-decoration:underline;text-underline-offset:5px}
.harbor-scene .roof-lettering{paint-order:stroke fill;stroke:#fff8e5;stroke-width:3;stroke-linejoin:round}
.harbor-scene a:hover .link-arrow{transform:translate(2px,-2px)}
.harbor-scene .link-arrow{transition:transform 180ms cubic-bezier(.16,1,.3,1)}
@keyframes harbor-current{to{stroke-dashoffset:-180}}
.harbor-scene .water-current{animation:harbor-current 24s linear infinite}
@media(prefers-reduced-motion:reduce){.harbor-scene .water-current{animation:none}.harbor-scene .link-arrow{transition:none}}
</style>
<defs>
 <filter id="harbor-shadow" x="-30%" y="-20%" width="170%" height="160%"><feDropShadow dx="2" dy="3" stdDeviation="1.6" flood-color="#182b2c" flood-opacity=".36"/></filter>
 <linearGradient id="harbor-roof-light"><stop stop-color="#d1c5a7"/><stop offset=".2" stop-color="#fffced"/><stop offset=".57" stop-color="#fff9e7"/><stop offset="1" stop-color="#ebdfc4"/></linearGradient>
 <linearGradient id="harbor-cargo-light"><stop stop-color="#000" stop-opacity=".24"/><stop offset=".35" stop-color="#fff" stop-opacity=".12"/><stop offset="1" stop-color="#000" stop-opacity=".1"/></linearGradient>
 <pattern id="harbor-roof-ribs" width="7" height="12" patternUnits="userSpaceOnUse"><rect width="7" height="12" fill="url(#harbor-roof-light)"/><path d="M.5 0V12" stroke="#af9e7b" stroke-opacity=".42" stroke-width=".65"/></pattern>
 <pattern id="harbor-cargo-ribs" width="5" height="10" patternUnits="userSpaceOnUse"><rect width="5" height="10" fill="url(#harbor-cargo-light)"/></pattern>
 ${drawWaterPattern()}
 <pattern id="harbor-concrete" width="94" height="76" patternUnits="userSpaceOnUse">
  <rect width="94" height="76" fill="#bdb9ab"/>
  <path d="M0 38H94M46 0V38M76 38V76" stroke="#8f9084" stroke-opacity=".25"/>
  <path d="M0 39H94M47 0V38M77 39V76" stroke="#e5dfcd" stroke-opacity=".32"/>
  <g fill="#756f5d" opacity=".24"><circle cx="9" cy="14" r=".8"/><circle cx="28" cy="5" r=".5"/><circle cx="36" cy="29" r=".65"/><circle cx="66" cy="12" r=".8"/><circle cx="84" cy="30" r=".6"/><circle cx="16" cy="55" r=".6"/><circle cx="54" cy="64" r=".75"/><circle cx="84" cy="60" r=".7"/><circle cx="62" cy="45" r=".6"/></g>
  <path d="m22 67 5-2m24-45 3 2m28 45 3-1" stroke="#eee7d2" stroke-width="1" opacity=".55"/>
 </pattern>
 <pattern id="harbor-hatching" width="16" height="16" patternUnits="userSpaceOnUse"><path d="M-4 12 4 20M0 0 16 16M12-4 20 4" fill="none" stroke="#efc94e" stroke-width="1.4"/></pattern>
 <pattern id="harbor-rail" width="20" height="18" patternUnits="userSpaceOnUse"><rect width="20" height="18" fill="#6a675d"/><path d="M1 3H19M1 12H19" stroke="#493c31" stroke-width="4"/><path d="M4 0V18M16 0V18" stroke="#2e3534" stroke-width="3.8"/><path d="M3 0V18M15 0V18" stroke="#c3bca8" stroke-width="1.4"/></pattern>
 <g id="harbor-casting"><rect width="5" height="5" rx=".7" fill="#786f58"/><path d="M1 4V1H4" fill="none" stroke="#fffbea" stroke-width="1"/><circle cx="3" cy="2.5" r=".9" fill="#263b3a"/></g>
 <g id="harbor-star" fill="currentColor"><path d="M9 0 12 6 18.6 7 13.8 11.7 15 18.3 9 15.1 3 18.3 4.2 11.7-.6 7 6 6Z"/></g>
 <g id="harbor-eye" fill="none" stroke="currentColor" stroke-width="2.4"><path d="M0 8Q10-5 20 8Q10 21 0 8Z"/><circle cx="10" cy="8" r="3.2" fill="currentColor" stroke="none"/></g>
 <g id="harbor-arrow" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M8 1H19V12M19 1 7 13M12 6H1V21H16V14"/></g>
 <g id="harbor-bollard" filter="url(#harbor-shadow)"><rect x="-6" y="-5" width="12" height="13" rx="3" fill="#ebe7d4" stroke="#465955"/><path d="M-4-2V4Q0 8 4 4V-2" fill="#eac032" stroke="#716a38"/><ellipse cy="-2" rx="5" ry="6" fill="#ffd64c" stroke="#5e663c"/><path d="M-2-6Q-4-4-3-1" fill="none" stroke="#fff1a8"/></g>
 <g id="harbor-drain"><rect width="9" height="12" fill="#707c7b" stroke="#ddd6bf" stroke-width=".8"/><path d="M2 2V10M4.5 2V10M7 2V10" stroke="#36494c" stroke-width=".7"/></g>
 <symbol id="harbor-cargo" viewBox="0 0 36 88" preserveAspectRatio="none"><rect x="1" y="1" width="34" height="86" rx="1" fill="currentColor" stroke="#32443f" stroke-width="1.3"/><rect x="3" y="3" width="30" height="82" fill="url(#harbor-cargo-ribs)"/><path d="M3 2H33M3 86H33" stroke="#f6e9c4" stroke-opacity=".5"/><path d="M4 4V84M32 4V84" stroke="#193f43" stroke-opacity=".3"/><path d="M4 79H32" stroke="#263d39" stroke-opacity=".4"/></symbol>
 <g id="harbor-pallet"><rect width="16" height="20" fill="#d6af6a" stroke="#5c6656" stroke-width=".9"/><path d="M2 2H14V18H2ZM8 2V18M2 7H14M2 13H14" fill="none" stroke="#8f7445" stroke-width=".8"/><path d="M4 3V5M10 8V11M4 14V17" stroke="#f7dc9b"/></g>
 <g id="harbor-pipes"><rect width="19" height="34" fill="#334c52" stroke="#cecbb6"/><g fill="#173f4d" stroke="#869a94" stroke-width="1"><circle cx="5" cy="6" r="3.7"/><circle cx="14" cy="6" r="3.7"/><circle cx="5" cy="16" r="3.7"/><circle cx="14" cy="16" r="3.7"/><circle cx="5" cy="26" r="3.7"/><circle cx="14" cy="26" r="3.7"/></g></g>
 <g id="harbor-crane" filter="url(#harbor-shadow)">
  <path d="M-18-29H19V29H-18Z" fill="#b94823" stroke="#703a25" stroke-width="1.6"/>
  <path d="M-15-25H16V25H-15Z" fill="#ee792f" stroke="#ffb162" stroke-width="1.6"/>
  <path d="M-19-19H-14M-19 19H-14M15-19H21M15 19H21" stroke="#444945" stroke-width="5"/>
  <rect x="-8" y="-22" width="13" height="44" fill="#df5b27" stroke="#913f21"/>
  <path d="M-6-13V-91H4V-13" fill="#ef7834" stroke="#983e23" stroke-width="1.5"/>
  <path d="M-3-18V-83" stroke="#ffaf59" stroke-width="2"/>
  <rect x="-2" y="-9" width="104" height="18" rx="1" fill="#d75025" stroke="#713b27" stroke-width="1.3"/>
  <path d="M10-6H98V6H10Z" fill="#53706a" stroke="#ff9b4b" stroke-width="1.4"/>
  <path d="M11-6 24 6 37-6 50 6 63-6 76 6 89-6 99 4M11 6 24-6 37 6 50-6 63 6 76-6 89 6 99-4" fill="none" stroke="#f07d38" stroke-width="1.5"/>
  <rect x="-10" y="-11" width="21" height="23" fill="#ed7c34" stroke="#773e24" stroke-width="1.5"/>
  <path d="M-7-8H7V8H-7Z" fill="#b74d26"/><path d="M-7-8H7V-3H-7Z" fill="#ffad57"/>
  <rect x="95" y="-5" width="4" height="10" fill="#f7bd74"/>
 </g>
 <g id="harbor-vehicle" filter="url(#harbor-shadow)">
  <path d="M-8-12V-4M8-12V-4M-8 8V15M8 8V15" stroke="#263d3c" stroke-width="3"/>
  <rect x="-7" y="-21" width="14" height="42" rx="3" fill="currentColor" stroke="#465448" stroke-width="1.1"/>
  <path d="M-5-9H5L4-2H-4ZM-4 11H4L5 15H-5Z" fill="#214554"/>
  <path d="M-5-19H5M-5 18H5" stroke="#fff2b5" stroke-width="1.5"/><path d="M-5 0V9M5 0V9" stroke="#927f3b"/>
 </g>
 <g id="harbor-tree" filter="url(#harbor-shadow)">
  <path d="M-3-23Q4-30 10-23Q20-25 21-15Q29-12 23-4Q29 4 21 10Q21 21 11 20Q6 28-2 21Q-12 28-17 18Q-27 18-25 8Q-32 1-23-7Q-26-17-15-18Q-13-28-3-23Z" fill="#27653b" stroke="#1d4b3f" stroke-width="1.4"/>
  <path d="M-3-22Q5-25 9-18Q18-22 19-12Q25-6 18 0Q23 7 14 10Q15 18 7 17Q0 23-5 16Q-14 21-16 12Q-25 11-21 3Q-27-5-17-8Q-20-18-11-16Q-10-25-3-22Z" fill="#579844"/>
  <path d="m-12-12 6-4 4 6-5 4Zm13-3 6-4 4 7-6 2ZM-4 0 2-4 5 3-2 7Zm-11 10 4-4 4 5-5 4Zm22 0 6-3 2 6-6 2Z" fill="#80ae4f"/>
  <path d="m-18-3 3-2m9-17 3 2M4-9 2-1m-5 22 3-2m12-13 2 2" stroke="#bad17a" stroke-width="2"/>
 </g>
 <g id="harbor-palm" fill="#2c8051" stroke="#18533d" stroke-width="1.1"><path d="M0 0Q-22-27-11-28Q-2-19 0 0Q4-31 12-25Q9-12 0 0Q25-24 27-14Q13-4 0 0Q33-3 29 6Q13 8 0 0Q24 19 16 24Q5 16 0 0Q4 32-5 28Q-9 13 0 0Q-24 25-27 15Q-15 3 0 0Q-32 4-29-5Q-13-8 0 0Z"/><circle r="3" fill="#8baf51"/></g>
</defs>
"""
