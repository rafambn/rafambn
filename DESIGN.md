---
name: Rafael Mendonça's open-source harbor
description: A detailed orthographic SVG port with six ivory project containers.
colors:
  ink: "#062c43"
  paper: "#fcf5e2"
  water: "#087fa5"
  concrete: "#bdb9ab"
  roof-ivory: "#fff7df"
  roof-border: "#585f50"
  lettering-outline: "#fff8e5"
  hull-red: "#c8482e"
  deck-green: "#388b70"
  crane-orange: "#ee792f"
  bay-yellow: "#edc950"
  rope-yellow: "#f7ce36"
  road: "#686f6c"
  yard: "#798969"
  foliage: "#579844"
  divider: "#d3cab4"
typography:
  display:
    fontFamily: "HarborSerif, serif"
    fontSize: "64px"
    fontWeight: 700
    letterSpacing: "-1.5px"
  display-mobile:
    fontFamily: "HarborSerif, serif"
    fontSize: "36px"
    fontWeight: 700
    letterSpacing: "-0.8px"
  display-readme:
    fontFamily: "HarborSerif, serif"
    fontSize: "63px"
    fontWeight: 700
    letterSpacing: "-1.5px"
  body:
    fontFamily: "HarborSans, sans-serif"
    fontSize: "23px"
    fontWeight: 400
  body-mobile:
    fontFamily: "HarborSans, sans-serif"
    fontSize: "17px"
    fontWeight: 400
  title:
    fontFamily: "Harbor, sans-serif"
    fontSize: "38px"
    fontWeight: 600
  title-mobile:
    fontFamily: "Harbor, sans-serif"
    fontSize: "29px"
    fontWeight: 600
  title-readme:
    fontFamily: "Harbor, sans-serif"
    fontSize: "39px"
    fontWeight: 600
  value:
    fontFamily: "Harbor, sans-serif"
    fontSize: "29px"
    fontWeight: 600
  value-mobile:
    fontFamily: "Harbor, sans-serif"
    fontSize: "24px"
    fontWeight: 600
rounded:
  casting: "0.7px"
  roof: "2px"
  focus: "3px"
spacing:
  page-top: "20px"
  page-side: "24px"
  page-bottom: "32px"
components:
  profile-header:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    typography: "{typography.display}"
  project-container:
    backgroundColor: "{colors.roof-ivory}"
    textColor: "{colors.ink}"
    rounded: "{rounded.roof}"
    typography: "{typography.title}"
  cargo-ship:
    width: "184px"
    height: "680px"
  harbor-ground:
    backgroundColor: "{colors.concrete}"
  github-footer:
    textColor: "{colors.ink}"
---

# Design System: Rafael Mendonça's open-source harbor

## Overview

**Creative North Star: "Open-source harbor"**

The portfolio follows the supplied harbor sketch: a detailed port viewed directly from above, with bright patterned water, one red cargo ship, and six ivory project roofs. The ship has a green deck, a forward white bridge, and colored cargo. Orange cranes, yellow loading bays and moorings, concrete, rail, road, vehicles, warehouses, and greenery fill the surrounding port.

The name uses a substantial serif face. Condensed navy lettering sits directly on the corrugated project roofs, with stars and views shown as icons and values. All text stays horizontal. The material detail is part of the identity, including on mobile, where the outer road and yard are reduced first.

**Key Characteristics:**

- Orthographic geometry with detailed SVG materials and horizontal text.
- A red and green ship beside six matching ivory project roofs.
- Serif profile name, condensed project lettering, and regular sans-serif phrase.
- Shared scene coordinates and seamless patterns across image strips.

This records `src/render/HarborLayout.kt`, `HarborDetails.kt`, `HarborDrawing.kt`, `HarborShip.kt`, `HarborSvg.kt`, and `HarborPage.kt`. SVG geometry, type sizes, and radii use SVG user units. Frontmatter `px` values for those properties are authoring values that scale with the SVG. Page spacing and media queries use rendered CSS pixels.

## Colors

Navy carries text, icons, and focus outlines. Cream paper surrounds the header, bright blue water borders the gray concrete quay, and every project roof uses the same ivory treatment. Projects have no individual color assignment.

The ship's red hull and green deck remain visible around its colored cargo. Cranes use layered orange tones; yellow marks loading bays, ropes, and fittings. Road gray and green yard and foliage colors continue the working port at the right edge. The frontmatter captures recurring base colors. Local highlights, shadows, caustic pools, and machinery details remain in `HarborDetails.kt` and `HarborShip.kt`.

## Typography

`Harbor` is embedded Barlow Condensed SemiBold at weight 600 in every SVG. `HarborSerif` is Source Serif 4 Bold at weight 700 for the name. `HarborSans` is Barlow Regular at weight 400 for the phrase. The latter two fonts are embedded only when an SVG includes the header. Numerals use `tabular-nums`.

The frontmatter contains profile name, phrase, project title, and project value sizes. The wide README phrase uses 22 units. Mobile splits the phrase into two lines with a 23-unit baseline gap. Profile period labels use 18 units, or 16 on mobile. Profile values use 32 units, or 28 on mobile, reduced by 5 when compact notation exceeds five characters. Footer text uses 22 units, or 20 on mobile.

Project titles are centered with an 11-unit left offset to leave room for the external-link icon. A 3-unit pale stroke painted beneath the title and values separates the lettering from the ribs. SVG baselines place the text; there is no CSS line-height scale.

## Layout

| Layout | SVG width × height | Header | Sea width | Project rows × columns | Row height | Footer |
| --- | --- | --- | --- | --- | --- | --- |
| Website desktop | 1200 × 1064 | 184 | 280 | 3 × 2 | 260 | 100 |
| Website mobile | 360 × 1384 | 204 | 82 | 6 × 1 | 184 | 76 |
| README wide | 840 × 1740 | 216 | 270 | 6 × 1 | 236 | 108 |

Website desktop uses a centered main element with a maximum width of 1344 CSS pixels and the page spacing tokens. At viewport widths of 900 CSS pixels or less, it shows the mobile SVG, removes page padding, and caps the main element at 560 CSS pixels. The SVG fills available width while preserving its aspect ratio. At 320 CSS pixels, mobile authoring values scale by 320/360.

Desktop roofs measure 311 × 180 units with a 42-unit column gap. Mobile roofs measure 212 × 132 units. README wide roofs measure 308 × 156 units. Roofs begin 40 units below each row boundary, or 26 on mobile. Yellow rectangles mark the loading bays around the roofs. A crane occupies the quay edge beside each project row. The outer rail and road remain narrow on mobile; its yard, warehouses, vehicles, and trees are omitted.

The README uses eight vertically adjacent linked images: header, six project strips, and footer. Both sizes have one project column. A `picture` media query selects mobile art at viewport widths of 760 CSS pixels or less. This measures the viewport, not GitHub's content column. Each strip crops the same global scene with its `viewBox`; ship, quay, road, and water continue across strip edges.

## Elevation & Depth

The projection stays top-down. Corrugation gradients, small edge highlights, outlines, and SVG drop shadows describe materials. The shared shadow uses an offset of 2 units right and 3 down, a standard deviation of 1.6, and dark color at 0.36 opacity. It appears beneath roofs, ship hull, bridge, cranes, bollards, vehicles, and trees. Hover brightens a roof without moving it.

## Shapes

Project roofs have small rounded corners, vertical corrugation repeated every 7 units, and four corner castings. Cargo uses a separate 5-unit rib pattern. Lettering remains over the ribs with a pale outline, rather than solid text masks.

The ship has a pointed bow and tapered rounded stern, with the bridge ahead of the cargo. Desktop cargo has four columns and six rows; mobile has two columns and twelve rows; README has four columns and eleven rows. The stepped quay end, mooring lines, machinery, and fittings share the same orthographic geometry.

Water uses two tapered branching caustic shapes with varied rotations, scales, and colored pools. A 160-unit tile repeats neighboring clusters at its edges to keep the pattern seamless. Concrete combines joint lines, speckles, and small scratches. These details are authored as reusable SVG definitions.

## Components

### Profile header

The serif name and phrase appear above the harbor on paper. Desktop places Today, Week, Month, and Total counts to their right. Mobile and README place the four counts below the phrase, with thin vertical dividers. Compact visible counts have full values in accessible descriptions. Desktop and README retain the road and rail beside the header.

### Project container

The whole roof is a native SVG anchor with a transparent hit rectangle extending 8 units beyond it. Order is KMaP, FrameBar, KFlate, KeyManager, Scribe, then wg-kotlin. A centered project name and external-link icon sit above star and eye icons with their values. Repository descriptions remain in accessible labels.

Hover and keyboard focus brighten the roof to 1.045 and underline the name. Keyboard focus also reveals a 3-unit navy outline positioned 7 units outside the roof. Hover moves the external-link icon 2 units right and 2 up over 180 milliseconds using `cubic-bezier(.16,1,.3,1)`. Accessible labels include full counts and the destination. Unknown stars use an em dash and "GitHub stars unavailable".

### Harbor ground and ship

Reusable definitions draw water, concrete, corrugated cargo, cranes, vehicles, trees, pallets, and fittings. The ship and environment are decorative and hidden from assistive technology. The water current changes its dash offset by 180 units over 24 seconds with linear timing. Reduced motion stops that animation and removes the arrow transition; the hover offset remains.

### GitHub footer and image strips

The footer has a native anchor, external-link icon, and visible keyboard outline. README anchors wrap each SVG image because links inside an SVG image are not interactive. SVG titles and descriptions, plus surrounding image alt text, describe the content.

The sidecar's five samples come directly from rendered preview SVGs and retain their embedded fonts. Sample counts are a preview snapshot. Synthesized tonal ramps are design-panel metadata, not extra application colors.

## Do's and Don'ts

- Do preserve the sketch's material detail, navy lettering, ivory roofs, orange cranes, and red and green ship.
- Do retain horizontal text, project order, the forward bridge, and the ship on the left.
- Do crop strips from shared layout coordinates and preserve continuous water and concrete patterns.
- Do preserve native links, visible keyboard focus, accessible count descriptions, and reduced-motion behavior.
- Don't assign separate pastel colors to project roofs; all six use the same ivory material.
- Don't replace unknown stars with invented values; keep the dash and unavailable description.
- Don't apply the website's two-column desktop grid to README images; use its single-column wide composition.
- Don't treat SVG authoring units as fixed CSS pixels when judging mobile legibility.
