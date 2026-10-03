package com.rafambn.profilebanner.render

fun renderKeymanagerBackground(width: Int, surface: String): String = """
    <g clip-path="url(#badge-clip)" aria-hidden="true">
      <defs>
        <linearGradient id="shackle-silver" x1="0" y1="6" x2="0" y2="26" gradientUnits="userSpaceOnUse">
          <stop offset="0" stop-color="#dce5ec"/>
          <stop offset="0.5" stop-color="#a5b5c2"/>
          <stop offset="1" stop-color="#dce5ec"/>
        </linearGradient>
      </defs>
      <rect width="$width" height="32" fill="$surface"/>
      <path d="M84 8H17A8 8 0 0 0 17 24H84" fill="none" stroke="url(#shackle-silver)" stroke-width="4"/>
      <rect x="80" y="4" width="${width - 84}" height="24" rx="4" fill="#0061a4"/>
    </g>
""".trimIndent()
