# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Product Purpose

Rafael Mendonça's portfolio presents six open-source repositories and links directly
to them from the website and GitHub profile README. The binding brief is `PROMPT.txt`.

## Capabilities and Constraints

- Kotlin/Ktor, JDK 21, packaged for Termux. Public origin: `https://profile.rafambn.com`.
- Preserve H2 MVStore data and the existing profile/repository counter keys.
- Use cached real GitHub stars; retain successful values during API failures.
- Profile periods are today in UTC, trailing seven days, trailing thirty days, and all time.
- Previews and statistics queries are read-only. Split images must not multiply profile visits.
- Repository order: KMaP, FrameBar, KFlate, KeyManager, Scribe, wg-kotlin.
- The website uses three rows and two project columns on desktop, six rows and one
  project column on mobile. It works from 320 CSS pixels without horizontal scrolling.
- GitHub images need surrounding links. Document any README layout limitation.
- Current scope ends with a reviewable local implementation, build, and executable
  package. Commit, push, and remote deployment require a later request.

## Brand Commitments

Rafael Mendonça. “Solutions architect. Making complex things simple.”

A strictly orthographic top-down harbor, with one vertically oriented cargo ship
on the left and the six project containers on the quay to its right. All artwork
is maintainable SVG authored in code. Text reads normally, never in perspective.
The supplied original sketch defines the visual character: bright blue patterned
water, a red hull and green deck, a forward white bridge, colorful cargo, ivory
corrugated project roofs, navy condensed lettering, and a substantial serif name.
Orange cranes, yellow loading bays and ropes, concrete, road and rail, vehicles,
trees, and warehouses make the port feel inhabited. Preserve that material detail
when adapting the required grids; simplify the outer shoreline first on mobile.

## Accessibility & Inclusion

Project areas work with mouse, touch, and keyboard. Names, statistics, and the
profile identity remain legible on narrow screens. Standalone SVG images must
carry equivalent accessible descriptions.
