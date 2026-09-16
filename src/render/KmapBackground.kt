package com.rafambn.profilebanner.render

fun renderKmapBackground(width: Int, surface: String): String = """
    <g clip-path="url(#badge-clip)">
      <svg width="$width" height="32" viewBox="0 0 120 32" preserveAspectRatio="none" aria-hidden="true">
        <defs>
          <g id="map-tree" fill="#79ad70" stroke="#548e57" stroke-width="0.35">
            <circle r="1.15"/>
            <path d="M0-0.6V0.7M-0.5 0H0.5" fill="none"/>
          </g>
        </defs>
        <rect width="120" height="32" fill="#8fcddd"/>
        <path d="M38 0C34 6 48 8 44 15S31 25 35 32H120V0Z" fill="#b2e1e6"/>
        <path d="M43 0C39 6 53 8 49 15S36 25 40 32H120V0Z" fill="#f6e4ac" stroke="#dfce92" stroke-width="0.5"/>
        <path d="M51 0C46 6 61 9 57 17S45 26 48 32H120V0Z" fill="$surface"/>
        <path d="M66 0 83 0C79 6 88 8 83 14L77 18 73 27 60 32H53C53 25 65 20 64 13S60 5 66 0Z" fill="#b7d7a5" stroke="#9dc58d" stroke-width="0.4"/>
        <path d="M99 0H120V32H83L88 25 86 19 92 12Z" fill="#c4dbb0"/>
        <path d="M53 3 59 1 61 5 56 7Z M59 23 65 20 68 25 62 28Z" fill="#d8e6bb"/>
        <g fill="none" stroke="#acba8b" stroke-width="0.45">
          <path d="M105 2C98 3 96 7 93 12S89 24 97 29 116 31 119 23 115 7 109 4Z"/>
          <path d="M105 6C99 7 99 11 96 15S95 24 101 26 113 26 115 20 111 8 105 6Z"/>
          <path d="M105 10C101 11 102 14 99 17S100 23 105 23 112 20 110 16 108 11 105 10Z"/>
          <path d="M105 14C102 15 102 19 105 20S109 17 105 14Z"/>
        </g>
        <path d="M91 0C86 6 92 9 85 15S84 25 75 32" fill="none" stroke="#82bed0" stroke-width="1.8"/>
        <path d="M91 0C86 6 92 9 85 15S84 25 75 32" fill="none" stroke="#a9dce5" stroke-width="0.9"/>
        <g fill="none" stroke-linecap="round" stroke-linejoin="round">
          <path d="M57-2C51 5 66 8 62 17S50 27 55 34M61 10 73 8 83 10 96 7 122 9M57 26 69 24 79 26 91 30" stroke="#c3b493" stroke-width="2.8"/>
          <path d="M57-2C51 5 66 8 62 17S50 27 55 34M61 10 73 8 83 10 96 7 122 9M57 26 69 24 79 26 91 30" stroke="#fff9e9" stroke-width="1.8"/>
          <path d="M70-2 72 8M97 7 94 0M69 24 72 33" stroke="#ffffff" stroke-width="1.2"/>
          <path d="M67 17C71 13 75 13 78 16S73 23 78 24M94 26C91 20 97 12 106 9" stroke="#c19873" stroke-width="0.6" stroke-dasharray="1.4 1.1"/>
        </g>
        <g fill="#d9ccc2" stroke="#b8a99d" stroke-width="0.35">
          <path d="M58 1 61 0 62 3 59 4Z M64 3 67 2 68 5 65 6Z M73 2H76V5H73Z"/>
          <path d="M64 11 67 10 68 13 65 14Z M57 28 60 27 61 30 58 31Z M83 3H86V6H83Z"/>
        </g>
        <g>
          <use href="#map-tree" x="67" y="18"/><use href="#map-tree" x="70" y="14"/>
          <use href="#map-tree" x="74" y="12"/><use href="#map-tree" x="77" y="20"/>
          <use href="#map-tree" x="71" y="20"/><use href="#map-tree" x="64" y="29"/>
          <use href="#map-tree" x="79" y="4"/><use href="#map-tree" x="81" y="19"/>
          <use href="#map-tree" x="116" y="4"/><use href="#map-tree" x="117" y="28"/>
          <use href="#map-tree" x="89" y="29"/><use href="#map-tree" x="97" y="3"/>
        </g>
        <path d="M35 5 37 4 39 6 37 8 35 7Z M39 26 41 25 43 27 41 29Z" fill="#d4ddd4" stroke="#a4b8b1" stroke-width="0.4"/>
      </svg>
    </g>
""".trimIndent()
