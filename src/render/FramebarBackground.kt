package com.rafambn.profilebanner.render

fun renderFramebarBackground(width: Int, surface: String): String = """
    <g clip-path="url(#badge-clip)" aria-hidden="true">
      <defs>
        <pattern id="frame-ruler" width="24" height="32" patternUnits="userSpaceOnUse">
          <path d="M0 24V32M6 27V32M12 25.5V32M18 27V32" fill="none" stroke="#b2b7bf" stroke-width="2.25"/>
        </pattern>
      </defs>
      <rect width="$width" height="32" fill="$surface"/>
      <rect y="24" width="$width" height="8" fill="url(#frame-ruler)"/>
      <path d="M73 6V32" stroke="#f4c271" stroke-width="2"/>
      <path d="M69 0H77V4L73 8 69 4Z" fill="#f4c271"/>
    </g>
""".trimIndent()
