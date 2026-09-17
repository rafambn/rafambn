package com.rafambn.profilebanner.render

import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LaunchBaseTest {
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
