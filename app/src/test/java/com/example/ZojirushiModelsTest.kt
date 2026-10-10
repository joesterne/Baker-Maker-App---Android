package com.example

import com.example.data.model.BreadMakerModel
import com.example.data.model.LoafSize
import com.example.data.model.ZojirushiMenuCourse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ZojirushiModelsTest {

    @Test
    fun testZojirushiLineupCoverage() {
        val allModels = BreadMakerModel.values()
        assertTrue("Lineup should support comprehensive models", allModels.size >= 12)

        val zojirushiModels = allModels.filter { it.isZojirushi }
        assertTrue("Should support multiple Zojirushi models", zojirushiModels.size >= 11)

        // Verify key models exist
        val codes = zojirushiModels.map { it.modelCode }
        assertTrue(codes.contains("BB-PDC20"))
        assertTrue(codes.contains("BB-PAC20"))
        assertTrue(codes.contains("BB-CEC20"))
        assertTrue(codes.contains("BB-CCX20"))
        assertTrue(codes.contains("BB-KW20"))
        assertTrue(codes.contains("BB-V20"))
        assertTrue(codes.contains("BB-SSC10"))
        assertTrue(codes.contains("BB-ST10"))
        assertTrue(codes.contains("BB-HAC10"))
        assertTrue(codes.contains("BB-HE10"))
        assertTrue(codes.contains("BB-HD10"))
        assertTrue(codes.contains("BB-HA10"))
    }

    @Test
    fun testTwoPoundDualBladeModels() {
        val dualBladeModels = listOf(
            BreadMakerModel.ZOJIRUSHI_BB_PDC20,
            BreadMakerModel.ZOJIRUSHI_BB_PAC20,
            BreadMakerModel.ZOJIRUSHI_BB_CEC20,
            BreadMakerModel.ZOJIRUSHI_BB_CCX20,
            BreadMakerModel.ZOJIRUSHI_BB_KW20,
            BreadMakerModel.ZOJIRUSHI_BB_V20
        )

        dualBladeModels.forEach { model ->
            assertEquals("${model.modelCode} should have 2 blades", 2, model.bladeCount)
            assertEquals("${model.modelCode} should default to 2.0 lb size", LoafSize.TWO_LB, model.defaultLoafSize)
            assertEquals("${model.modelCode} max flour should be 560g", 560, model.maxFlourGrams)
            assertTrue(model.isZojirushi)
        }
    }

    @Test
    fun testOnePoundCompactModels() {
        val compactModels = listOf(
            BreadMakerModel.ZOJIRUSHI_BB_SSC10,
            BreadMakerModel.ZOJIRUSHI_BB_ST10,
            BreadMakerModel.ZOJIRUSHI_BB_HAC10,
            BreadMakerModel.ZOJIRUSHI_BB_HE10,
            BreadMakerModel.ZOJIRUSHI_BB_HD10,
            BreadMakerModel.ZOJIRUSHI_BB_HA10
        )

        compactModels.forEach { model ->
            assertEquals("${model.modelCode} should have 1 blade", 1, model.bladeCount)
            assertEquals("${model.modelCode} should default to 1.0 lb size", LoafSize.ONE_LB, model.defaultLoafSize)
            assertEquals("${model.modelCode} max flour should be 300g", 300, model.maxFlourGrams)
            assertTrue(model.isZojirushi)
        }
    }

    @Test
    fun testHeaterConfigurations() {
        // Flagship Virtuoso models have upper lid browning heaters
        assertTrue(BreadMakerModel.ZOJIRUSHI_BB_PDC20.hasLidHeater)
        assertTrue(BreadMakerModel.ZOJIRUSHI_BB_PAC20.hasLidHeater)

        // Classic Supreme and Mini models have bottom element heating
        assertFalse(BreadMakerModel.ZOJIRUSHI_BB_CEC20.hasLidHeater)
        assertFalse(BreadMakerModel.ZOJIRUSHI_BB_HAC10.hasLidHeater)
        assertFalse(BreadMakerModel.ZOJIRUSHI_BB_SSC10.hasLidHeater)
    }

    @Test
    fun testAutomaticDispenserModels() {
        // Maestro and Kobo Kitchen feature automated dispensers
        assertTrue(BreadMakerModel.ZOJIRUSHI_BB_SSC10.hasAutoDispenser)
        assertTrue(BreadMakerModel.ZOJIRUSHI_BB_ST10.hasAutoDispenser)

        // Traditional models rely on beep/chime add-in alerts
        assertFalse(BreadMakerModel.ZOJIRUSHI_BB_PDC20.hasAutoDispenser)
        assertFalse(BreadMakerModel.ZOJIRUSHI_BB_HAC10.hasAutoDispenser)
    }

    @Test
    fun testModelCoursesAndMapping() {
        // Test PDC20 course list
        val pdcCourses = BreadMakerModel.ZOJIRUSHI_BB_PDC20.getCourses()
        assertEquals(14, pdcCourses.size)
        assertEquals("White", pdcCourses[0].courseName)
        assertEquals("Sourdough Starter", pdcCourses[11].courseName)

        // Test mapping for European on Mini (BB-HAC10 maps to French bread #2)
        val miniEuropean = BreadMakerModel.ZOJIRUSHI_BB_HAC10.mapRecommendedCourse(ZojirushiMenuCourse.EUROPEAN)
        assertEquals(2, miniEuropean.courseNumber)
        assertEquals("French Bread", miniEuropean.courseName)

        // Test mapping for Sourdough Starter on Kobo (BB-ST10 maps to Natural Yeast Starter #4)
        val koboStarter = BreadMakerModel.ZOJIRUSHI_BB_ST10.mapRecommendedCourse(ZojirushiMenuCourse.SOURDOUGH_STARTER)
        assertEquals(4, koboStarter.courseNumber)
        assertEquals("Natural Yeast Starter", koboStarter.courseName)

        // Test mapping on Virtuoso (BB-PAC20 maps Gluten-Free to #3)
        val pacGlutenFree = BreadMakerModel.ZOJIRUSHI_BB_PAC20.mapRecommendedCourse(ZojirushiMenuCourse.GLUTEN_FREE)
        assertEquals(3, pacGlutenFree.courseNumber)
        assertEquals("Gluten Free", pacGlutenFree.courseName)
    }

    @Test
    fun testManualUrlsValid() {
        BreadMakerModel.values().forEach { model ->
            assertNotNull(model.manualUrl)
            assertTrue("${model.modelCode} manual URL should start with http", model.manualUrl.startsWith("http"))
        }
    }
}
