package com.example

import com.example.data.model.BakingStageType
import com.example.data.model.PreloadedBreadMachineCycles
import com.example.data.model.ZojirushiMenuCourse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BreadMachineCycleTimerTest {

    @Test
    fun testPreloadedBreadMachineCyclesCompleteness() {
        val allCycles = PreloadedBreadMachineCycles.ALL_CYCLES
        assertEquals(10, allCycles.size)

        // Verify key bread machine cycles exist
        val white = allCycles.find { it.course == ZojirushiMenuCourse.WHITE }
        assertNotNull(white)
        assertEquals("Basic White Bread Cycle", white!!.name)
        assertEquals(225, white.totalMinutes)
        assertEquals(225 * 60, white.totalDurationSeconds)

        val wholeWheat = allCycles.find { it.course == ZojirushiMenuCourse.WHOLE_WHEAT }
        assertNotNull(wholeWheat)
        assertEquals(2, wholeWheat!!.courseNumber)
        assertTrue(wholeWheat.stages.any { it.type == BakingStageType.PREHEAT })

        val european = allCycles.find { it.course == ZojirushiMenuCourse.EUROPEAN }
        assertNotNull(european)
        assertEquals("European / Artisan Sourdough Cycle", european!!.name)
        assertTrue(european.hasLidHeater)

        val glutenFree = allCycles.find { it.course == ZojirushiMenuCourse.GLUTEN_FREE }
        assertNotNull(glutenFree)
        assertEquals(145, glutenFree!!.totalMinutes)

        val dough = allCycles.find { it.course == ZojirushiMenuCourse.DOUGH }
        assertNotNull(dough)
        assertEquals(110, dough!!.totalMinutes)
        // Dough cycle does not bake
        assertTrue(dough.stages.none { it.type == BakingStageType.BAKE })
    }

    @Test
    fun testCycleStageProgressionAndDurations() {
        val whiteCycle = PreloadedBreadMachineCycles.WHITE_BREAD
        val stages = whiteCycle.stages

        assertEquals(10, stages.size)
        assertEquals(BakingStageType.PREHEAT, stages[0].type)
        assertEquals(BakingStageType.KNEAD_1, stages[1].type)
        assertEquals(BakingStageType.REST_AUTOLYSE, stages[2].type)
        assertEquals(BakingStageType.KNEAD_2, stages[3].type)
        assertEquals(BakingStageType.RISE_1, stages[4].type)
        assertEquals(BakingStageType.PUNCH_DOWN, stages[5].type)
        assertEquals(BakingStageType.RISE_2, stages[6].type)
        assertEquals(BakingStageType.RISE_3, stages[7].type)
        assertEquals(BakingStageType.BAKE, stages[8].type)
        assertEquals(BakingStageType.KEEP_WARM, stages[9].type)

        // Verify stage durations sum up to 225 minutes
        val sumMinutes = stages.sumOf { it.durationMinutes }
        assertEquals(225, sumMinutes)
        assertEquals(225 * 60, whiteCycle.totalDurationSeconds)
    }

    @Test
    fun testCycleStageTelemetryProperties() {
        val european = PreloadedBreadMachineCycles.EUROPEAN_SOURDOUGH

        // Kneading stage should have motor RPM
        val knead2 = european.stages.find { it.type == BakingStageType.KNEAD_2 }
        assertNotNull(knead2)
        assertEquals(120, knead2!!.motorRpm)

        // Proofing stages should have motor 0 RPM and controlled temperature
        val rise1 = european.stages.find { it.type == BakingStageType.RISE_1 }
        assertNotNull(rise1)
        assertEquals(0, rise1!!.motorRpm)
        assertEquals(82, rise1.targetTempF)

        // Bake stage should have high heat and heater power
        val bake = european.stages.find { it.type == BakingStageType.BAKE }
        assertNotNull(bake)
        assertEquals(395, bake!!.targetTempF)
        assertEquals(100, bake.heatingPowerPercent)
    }

    @Test
    fun testPaddleRemovalAndIngredientChimes() {
        val white = PreloadedBreadMachineCycles.WHITE_BREAD
        assertNotNull(white.paddleRemovalStageIndex)
        assertEquals(5, white.paddleRemovalStageIndex)
        val paddleStage = white.stages[white.paddleRemovalStageIndex!!]
        assertEquals(BakingStageType.PUNCH_DOWN, paddleStage.type)
        assertTrue(paddleStage.hasIngredientAlert)

        val multigrain = PreloadedBreadMachineCycles.MULTIGRAIN
        assertNotNull(multigrain.dispenserAlertStageIndex)
        val dispenserStage = multigrain.stages[multigrain.dispenserAlertStageIndex!!]
        assertTrue(dispenserStage.hasIngredientAlert)
        assertTrue(dispenserStage.alertTitle?.contains("Dispenser") == true)
    }

    @Test
    fun testProgressCalculations() {
        val cycle = PreloadedBreadMachineCycles.QUICK_RAPID
        val totalSecs = cycle.totalDurationSeconds
        assertEquals(118 * 60, totalSecs)

        // Beginning
        val startFraction = 0f / totalSecs.toFloat()
        assertEquals(0f, startFraction, 0.001f)

        // Midpoint
        val midElapsed = totalSecs / 2
        val midFraction = midElapsed.toFloat() / totalSecs.toFloat()
        assertEquals(0.5f, midFraction, 0.001f)

        // Stage progress
        val firstStage = cycle.stages.first()
        val firstStageTotal = firstStage.durationMinutes * 60 // 600s
        val stageElapsed = 300 // 5m in
        val stageFraction = stageElapsed.toFloat() / firstStageTotal.toFloat()
        assertEquals(0.5f, stageFraction, 0.001f)
    }
}
