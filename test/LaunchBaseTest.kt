package com.rafambn.profilebanner.render

import com.rafambn.profilebanner.RepositoryStats
import com.rafambn.profilebanner.counter.ViewStats
import com.rafambn.profilebanner.pinnedRepos
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertContains
import kotlin.test.assertTrue

class LaunchBaseTest {
    @Test
    fun profileHeaderShowsEachWindowAndPreservesExactAccessibleCounts() {
        val svg = renderLaunchBaseSvg(profileStats = ViewStats(29, 31, 12_345, Long.MAX_VALUE))
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(svg.byteInputStream())
        val groups = document.getElementsByTagName("g")
        val panel = (0 until groups.length).map { groups.item(it) as Element }
            .single { it.getAttribute("id") == "profile-views" }
        assertContains(document.documentElement.textContent, "Rafael Mendonça")
        assertContains(document.documentElement.textContent, "Solutions architect. Making complex things simple.")
        assertEquals("29", panel.getAttribute("data-today"))
        assertEquals("31", panel.getAttribute("data-week"))
        assertEquals("12345", panel.getAttribute("data-month"))
        assertEquals(Long.MAX_VALUE.toString(), panel.getAttribute("data-total"))
        assertContains(panel.textContent, "12.3k")
        assertContains(panel.textContent, "9.2E")
        assertContains(panel.getAttribute("aria-label"), "12.345 in the last 30 days")
        assertContains(panel.getAttribute("aria-label"), "9.223.372.036.854.775.807 total")
    }

    @Test
    fun repositoryCardsLinkToTheirReposAndDistinguishZeroFromUnavailableStars() {
        val svg = renderLaunchBaseSvg(repositoryStats = mapOf(
            "KMaP" to RepositoryStats(stars = 19, views = 12_345),
            "FrameBar" to RepositoryStats(stars = 0, views = Long.MAX_VALUE)
        ))
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(svg.byteInputStream())
        val anchors = document.getElementsByTagName("a")
        assertEquals(6, anchors.length)
        pinnedRepos.forEachIndexed { index, repository ->
            val anchor = anchors.item(index) as Element
            assertEquals("https://github.com/rafambn/$repository", anchor.getAttribute("href"))
            assertEquals("0", anchor.getAttribute("tabindex"))
        }
        val map = anchors.item(0) as Element
        assertContains(map.textContent, "12.3k")
        assertContains(map.getAttribute("aria-label"), "12.345 recorded views")
        val frameBar = anchors.item(1) as Element
        assertContains(frameBar.getAttribute("aria-label"), "0 GitHub stars")
        assertContains(frameBar.textContent, "9.2E")
        val kflate = anchors.item(2) as Element
        assertContains(kflate.getAttribute("aria-label"), "GitHub stars temporarily unavailable")
        assertContains(kflate.textContent, "—")
    }

    @Test
    fun bothLayoutsAreStandaloneVectorsWithResolvedReferences() {
        for (mobile in listOf(false, true)) {
            val svg = renderLaunchBaseSvg(mobile)
            val document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(svg.byteInputStream())
            assertEquals(if (mobile) "720" else "1600", document.documentElement.getAttribute("width"))
            assertEquals(0, document.getElementsByTagName("image").length)
            val elements = document.getElementsByTagName("*")
            val ids = (0 until elements.length).map {
                (elements.item(it) as org.w3c.dom.Element).getAttribute("id")
            }.filter(String::isNotEmpty)
            assertEquals(ids.size, ids.toSet().size)
            Regex("(?:href=\"#|url\\(#)([A-Za-z0-9_.:-]+)").findAll(svg).forEach {
                assertTrue(it.groupValues[1] in ids, "Missing SVG reference: ${it.value}")
            }
        }
    }
}
