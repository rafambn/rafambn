package com.rafambn.profilebanner.render

import com.rafambn.profilebanner.PortfolioStats
import com.rafambn.profilebanner.RepositoryStats
import com.rafambn.profilebanner.counter.ViewStats
import com.rafambn.profilebanner.pinnedRepos
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HarborSvgTest {
    private val stats = PortfolioStats(
        ViewStats(29, 31, 12_345, Long.MAX_VALUE),
        pinnedRepos.mapIndexed { index, name ->
            RepositoryStats(name, when (index) { 0 -> 19; 1 -> 0; else -> null }, ViewStats(total = 999_900))
        }
    )

    @Test
    fun layoutsKeepTheShipAndRepositoryOrderWhileChangingTheGrid() {
        for (layout in HarborLayout.entries) {
            val svg = renderHarborSvg(stats, layout)
            val document = parse(svg)
            val elements = document.getElementsByTagName("*")
            val all = (0 until elements.length).map { elements.item(it) as Element }
            val projects = all.filter { it.hasAttribute("data-repository") }
            assertEquals(pinnedRepos, projects.map { it.getAttribute("data-repository") })
            assertEquals(1, all.count { it.getAttribute("data-art") == "cargo-ship" })
            projects.forEachIndexed { index, project ->
                assertEquals((index / layout.columns).toString(), project.getAttribute("data-row"))
                assertEquals((index % layout.columns).toString(), project.getAttribute("data-column"))
                assertEquals("https://github.com/rafambn/${pinnedRepos[index]}", project.getAttribute("href"))
                assertEquals("0", project.getAttribute("tabindex"))
            }
            assertEquals(layout.width.toString(), document.documentElement.getAttribute("width"))
            assertTrue(svg.toByteArray().size < 128 * 1024, "$layout is ${svg.toByteArray().size} bytes; the standalone scene should stay under 128 KiB")
            for (tag in listOf("image", "script", "foreignObject")) assertEquals(0, document.getElementsByTagName(tag).length)
        }
    }

    @Test
    fun imageStripsShareAnExactCoordinateSystemAndContainOnlyTheirProjectLink() {
        for (layout in listOf(HarborLayout.README, HarborLayout.MOBILE)) {
            var end = layout.headerHeight
            for (index in pinnedRepos.indices) {
                val svg = renderHarborSvg(stats, layout, end, layout.rowHeight, index)
                val document = parse(svg)
                assertEquals("0 $end ${layout.width} ${layout.rowHeight}", document.documentElement.getAttribute("viewBox"))
                val links = document.getElementsByTagName("a")
                assertEquals(1, links.length)
                assertEquals("https://github.com/rafambn/${pinnedRepos[index]}", (links.item(0) as Element).getAttribute("href"))
                end += layout.rowHeight
            }
            assertEquals(layout.headerHeight + layout.bodyHeight, end)
            val footer = parse(renderHarborSvg(stats, layout, end, layout.footerHeight))
            assertEquals("0 $end ${layout.width} ${layout.footerHeight}", footer.documentElement.getAttribute("viewBox"))
        }
    }

    @Test
    fun inlineLayoutsHaveUniqueResolvedIdsAndCountsRemainAccessible() {
        val desktop = renderHarborSvg(stats, HarborLayout.DESKTOP)
        val mobile = renderHarborSvg(stats, HarborLayout.MOBILE)
        val svg = "<root>${desktop.substringAfter("?>")}${mobile.substringAfter("?>")}</root>"
        val elements = parse(svg).getElementsByTagName("*")
        val ids = (0 until elements.length).map { (elements.item(it) as Element).getAttribute("id") }.filter(String::isNotEmpty)
        assertEquals(ids.size, ids.toSet().size)
        Regex("(?:href=\"#|url\\(#)([A-Za-z0-9_.:-]+)").findAll(svg).forEach {
            assertTrue(it.groupValues[1] in ids, "Unresolved SVG reference: ${it.value}")
        }
        assertContains(svg, "0 GitHub stars")
        assertContains(svg, "GitHub stars unavailable")
        assertContains(svg, "999,900 recorded repository views")
        assertContains(svg, "9,223,372,036,854,775,807 total")
        assertContains(svg, "data:font/woff2;base64,")
        assertContains(svg, "prefers-reduced-motion:reduce")
    }

    private fun parse(svg: String) = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(svg.byteInputStream())
}
