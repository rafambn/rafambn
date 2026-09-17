package com.rafambn.profilebanner.render

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
        assertContains(svg, "fill=\"#f5ead2\"")
        assertContains(svg, "fill=\"#ffffff\"")
        assertTrue(!svg.contains("onload"))
    }

    @Test
    fun badgesShareDimensionsAndKeepFirstDigitFixedWhenGrowing() {
        com.rafambn.profilebanner.pinnedRepos.forEach { repository ->
            val svg = renderRepositoryBadge("rafambn/$repository", 42)
            javax.xml.parsers.DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(svg.byteInputStream())
            assertContains(svg, "width=\"120\" height=\"32\"")
            assertContains(svg, "<text x=\"90\" y=\"21\" textLength=\"16\" lengthAdjust=\"spacingAndGlyphs\">42</text>")
            assertContains(svg, "text-anchor=\"start\"")
            assertContains(svg, "monospace")
            assertTrue(!svg.contains(">$repository</text>"))
            assertTrue(!svg.contains("<image"))
            assertTrue(!svg.contains("data:image"))
        }
        val large = renderRepositoryBadge("rafambn/KMaP", Long.MAX_VALUE)
        assertContains(large, "width=\"304\" height=\"32\"")
        assertContains(large, "<text x=\"90\" y=\"21\" textLength=\"200\" lengthAdjust=\"spacingAndGlyphs\">9.223.372.036.854.775.807</text>")
    }

    @Test
    fun escapesRepositoryNames() {
        assertContains(renderRepositoryBadge("rafambn/KMaP&tools", 1234), "KMaP&amp;tools views: 1.234")
    }
}
