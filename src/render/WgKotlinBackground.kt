package com.rafambn.profilebanner.render

fun renderWgKotlinBackground(width: Int, surface: String): String = """
    <g clip-path="url(#badge-clip)" aria-hidden="true">
      <rect width="$width" height="32" fill="$surface"/>
      <g fill="none" stroke="#fb923c" stroke-linecap="round" stroke-linejoin="round">
        <path d="M0 5H51L57 8H67M0 27H54L63 22M83 22H94L103 27H$width" stroke="#facc15" stroke-width="2.5"/>
        <g transform="translate(53 -4) scale(0.4)" stroke-width="8">
          <path d="M35 30 50 21 65 30V45"/>
          <path d="M75 55V72L58 82 42 73"/>
          <path d="M25 73V55L38 48"/>
        </g>
      </g>
    </g>
""".trimIndent()
