package com.rafambn.profilebanner.render

// Quill geometry from rafambn/Scribe's scribe-logo.svg, fitted to the ink endpoint.
fun renderScribeBackground(width: Int, surface: String): String = """
    <g clip-path="url(#badge-clip)" aria-hidden="true">
      <rect width="$width" height="32" fill="$surface"/>
      <path d="M0 2H$width M0 30H$width" stroke="#c5a878" stroke-opacity="0.45" stroke-width="0.7"/>
      <path d="M9 0V32" stroke="#a16207" stroke-opacity="0.16" stroke-width="0.7"/>
      <path d="M14 26C25 24.5 37 27.5 47 25.5S58 26.5 66 25" fill="none" stroke="#0d9488" stroke-width="1" stroke-linecap="round"/>
      <defs>
        <mask id="scribe-feather-cuts" maskUnits="userSpaceOnUse" x="0" y="0" width="100" height="100">
          <rect width="100" height="100" fill="#ffffff"/>
          <path d="M22 20 40 34 24 28ZM28 38 44 48 30 44ZM36 53 47 60 38 58ZM60 22 49 34 56 28ZM62 40 51 49 58 45ZM58 55 51 62 55 59Z" fill="#000000"/>
        </mask>
        <mask id="scribe-nib-hole" maskUnits="userSpaceOnUse" x="0" y="0" width="100" height="100">
          <rect width="100" height="100" fill="#ffffff"/>
          <circle cx="50" cy="82" r="1.5" fill="#000000"/>
        </mask>
      </defs>
      <g transform="matrix(-0.281660 -0.149761 -0.149761 0.281660 94.010827 6.293665)">
        <g mask="url(#scribe-feather-cuts)">
          <path d="M35 10C28 25 40 60 47 70H49C49 50 44 20 35 10Z" fill="#0d9488"/>
          <path d="M35 10C46 20 51 50 51 70H53C58 60 55 25 35 10Z" fill="#a16207"/>
        </g>
        <path d="M47 69H49V79H47Z" fill="#0d9488"/>
        <path d="M51 69H53V79H51Z" fill="#a16207"/>
        <g mask="url(#scribe-nib-hole)">
          <path d="M47 78 45 84Q47 89 50 93L49 78Z" fill="#0d9488"/>
          <path d="M53 78 55 84Q53 89 50 93L51 78Z" fill="#a16207"/>
        </g>
      </g>
    </g>
""".trimIndent()
