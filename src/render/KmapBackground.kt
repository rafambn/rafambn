package com.rafambn.profilebanner.render

fun renderKmapBackground(width: Int, surface: String): String = """
    <g clip-path="url(#badge-clip)">
      <svg width="$width" height="32" viewBox="0 0 120 32" preserveAspectRatio="none" aria-hidden="true">
        <rect width="120" height="32" fill="#8fcddd"/>
        <path d="M38 0C34 6 48 8 44 15S31 25 35 32H120V0Z" fill="#b2e1e6"/>
        <path d="M43 0C39 6 53 8 49 15S36 25 40 32H120V0Z" fill="#f6e4ac" stroke="#dfce92" stroke-width="0.5"/>
        <path d="M51 0C46 6 61 9 57 17S45 26 48 32H120V0Z" fill="$surface"/>
        <path d="M66 0 83 0C79 6 88 8 83 14L77 18 73 27 60 32H53C53 25 65 20 64 13S60 5 66 0Z" fill="#b7d7a5" stroke="#9dc58d" stroke-width="0.4"/>
        <path d="M99 0H120V32H83L88 25 86 19 92 12Z" fill="#c4dbb0"/>
        <path d="M53 3 59 1 61 5 56 7Z M59 23 65 20 68 25 62 28Z" fill="#d8e6bb"/>
        <path d="M91 0C86 6 92 9 85 15S84 25 75 32" fill="none" stroke="#82bed0" stroke-width="1.8"/>
        <path d="M91 0C86 6 92 9 85 15S84 25 75 32" fill="none" stroke="#a9dce5" stroke-width="0.9"/>
        <g fill="none" stroke-linecap="round" stroke-linejoin="round">
          <path d="M62 10C68 9 71 7 77 8S84 11 91 8 108 5 122 7M57 26C65 22 69 23 75 25S84 28 91 30M70-2 72 8M96 6 94-2M69 24 72 34" stroke="#b7b7a4" stroke-width="2"/>
          <path d="M62 10C68 9 71 7 77 8S84 11 91 8 108 5 122 7M57 26C65 22 69 23 75 25S84 28 91 30M70-2 72 8M96 6 94-2M69 24 72 34" stroke="#fffef5" stroke-width="1.25"/>
          <path d="M57-2C51 5 66 8 62 17S50 27 55 34" stroke="#c3a475" stroke-width="3.2"/>
          <path d="M57-2C51 5 66 8 62 17S50 27 55 34" stroke="#f9e9b8" stroke-width="2.2"/>
          <path d="M57-2C51 5 66 8 62 17S50 27 55 34" stroke="#fff6d8" stroke-width="0.6"/>
          <path d="M85.5 8.6 89.5 7.5M86 10.8 90 9.7" stroke="#89968e" stroke-width="0.45"/>
        </g>
        <g fill="#d9ccc2" stroke="#b8a99d" stroke-width="0.35">
          <path d="M58 1 61 0 62 3 59 4Z M64 3 67 2 68 5 65 6Z M73 2H76V5H73Z"/>
          <path d="M64 11 67 10 68 13 65 14Z M57 28 60 27 61 30 58 31Z M83 3H86V6H83Z"/>
        </g>
      </svg>
    </g>
""".trimIndent()
