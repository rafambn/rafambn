package com.rafambn.profilebanner.render

fun renderKflateBackground(width: Int, surface: String): String = """
    <g clip-path="url(#badge-clip)" aria-hidden="true">
      <defs>
        <linearGradient id="compression-flow" x1="0" y1="0" x2="120" y2="0" gradientUnits="userSpaceOnUse">
          <stop offset="0" stop-color="#00b4db" stop-opacity="0.3"/>
          <stop offset="0.4" stop-color="#00e676" stop-opacity="0.45"/>
          <stop offset="0.58" stop-color="#36e6bf"/>
          <stop offset="0.75" stop-color="#00cbbd" stop-opacity="0.8"/>
          <stop offset="1" stop-color="#00b4db" stop-opacity="0.45"/>
        </linearGradient>
      </defs>
      <rect width="$width" height="32" fill="$surface"/>
      <g fill="none" stroke="url(#compression-flow)" stroke-width="1.6">
        <path d="M-4-16C29-16 46-9 60 9S74 22 88 22H$width"/>
        <path d="M-4-5C27-5 43-3 57 12S74 24 88 24H$width"/>
        <path d="M-4 6C25 6 40 4 54 15S74 26 88 26H$width"/>
        <path d="M-4 26C23 26 39 12 53 20S74 28 88 28H$width"/>
        <path d="M-4 37C23 37 39 21 54 25S74 30 88 30H$width"/>
        <path d="M-4 48C23 48 42 29 57 30S74 32 88 32H$width"/>
      </g>
    </g>
""".trimIndent()
