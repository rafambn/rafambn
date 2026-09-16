package com.rafambn.profilebanner.render

import com.rafambn.profilebanner.counter.ViewStats
import com.rafambn.profilebanner.profile.ProfileSnapshot
import com.rafambn.profilebanner.profile.RepositorySnapshot
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class SvgRendererTest {
    @Test
    fun customBadgeRejectsInjectedColors() {
        val svg = renderRepositoryBadge(
            "rafambn/Scribe", 42,
            background = "\" onload=\"alert(1)",
            textColor = "ffffff"
        )
        assertContains(svg, "fill=\"#351b29\"")
        assertContains(svg, "fill=\"#ffffff\"")
        assertTrue(!svg.contains("onload"))
    }

    @Test
    fun badgesShareDimensionsAndKeepFirstDigitFixedWhenGrowing() {
        com.rafambn.profilebanner.pinnedRepos.forEach { repository ->
            val svg = renderRepositoryBadge("rafambn/$repository", 42)
            assertContains(svg, "width=\"120\" height=\"32\"")
            assertContains(svg, "<text x=\"90\" y=\"21\" textLength=\"16\" lengthAdjust=\"spacingAndGlyphs\">42</text>")
            assertContains(svg, "text-anchor=\"start\"")
            assertContains(svg, "monospace")
            assertTrue(!svg.contains(">$repository</text>"))
        }
        val large = renderRepositoryBadge("rafambn/KMaP", Long.MAX_VALUE)
        assertContains(large, "width=\"304\" height=\"32\"")
        assertContains(large, "<text x=\"90\" y=\"21\" textLength=\"200\" lengthAdjust=\"spacingAndGlyphs\">9.223.372.036.854.775.807</text>")
    }

    @Test
    fun rendersResponsiveProfileAndEscapedBadge() {
        val profile = ProfileSnapshot(
            listOf(RepositorySnapshot("KMaP", "A useful library", 12, "Kotlin"))
        )
        val mobileSvg = renderProfileSvg(
            profile = profile,
            profileStats = ViewStats(today = 1, week = 2, month = 3, total = 4),
            repositoryViews = listOf(5),
            mobile = true
        )
        val badgeSvg = renderRepositoryBadge("rafambn/KMaP&tools", 1234)

        assertTrue(mobileSvg.startsWith("<?xml"))
        assertContains(mobileSvg, "width=\"720\" height=\"1548\"")
        assertContains(mobileSvg, "★ 12")
        assertContains(mobileSvg, "views 5")
        assertContains(mobileSvg, "@keyframes metric-enter")
        assertContains(mobileSvg, "@media (prefers-reduced-motion: reduce)")
        assertContains(mobileSvg, "class=\"repository-card repository-card-0\"")
        assertContains(badgeSvg, "KMaP&amp;tools views: 1.234")
    }
}
