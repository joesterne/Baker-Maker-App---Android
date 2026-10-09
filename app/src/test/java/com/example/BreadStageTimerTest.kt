package com.example

import com.example.data.model.BreadStageTimer
import com.example.data.model.PreloadedTimerRoutines
import com.example.data.model.TimerStageCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BreadStageTimerTest {

    @Test
    fun testStageTimerFormatting() {
        // 45 minutes = 2700s
        val timer = BreadStageTimer(
            name = "Autolyse",
            category = TimerStageCategory.AUTOLYSE,
            targetDurationSeconds = 45 * 60,
            remainingSeconds = 45 * 60
        )
        assertEquals("45:00", timer.formattedRemaining)
        assertEquals("45 min", timer.formattedTargetDuration)
        assertEquals(0f, timer.progressFraction, 0.001f)

        // Halfway through: 22m 30s remaining
        val halfwayTimer = timer.copy(remainingSeconds = 1350)
        assertEquals("22:30", halfwayTimer.formattedRemaining)
        assertEquals(0.5f, halfwayTimer.progressFraction, 0.001f)

        // 1 hour 15 minutes = 4500s
        val longTimer = BreadStageTimer(
            name = "Bulk Fermentation",
            category = TimerStageCategory.BULK_PROOF,
            targetDurationSeconds = 75 * 60,
            remainingSeconds = 75 * 60
        )
        assertEquals("01:15:00", longTimer.formattedRemaining)
        assertEquals("1h 15m", longTimer.formattedTargetDuration)
    }

    @Test
    fun testPreloadedRoutinesCompleteness() {
        val sourdough = PreloadedTimerRoutines.ARTISAN_SOURDOUGH
        assertTrue(sourdough.stages.isNotEmpty())
        assertTrue(sourdough.stages.any { it.category == TimerStageCategory.KNEADING })
        assertTrue(sourdough.stages.any { it.category == TimerStageCategory.BULK_PROOF })
        assertTrue(sourdough.stages.any { it.category == TimerStageCategory.FINAL_PROOF })
        assertTrue(sourdough.stages.any { it.category == TimerStageCategory.BAKING })

        val quickYeast = PreloadedTimerRoutines.QUICK_YEAST_BREAD
        assertTrue(quickYeast.stages.any { it.category == TimerStageCategory.KNEADING })
        assertTrue(quickYeast.stages.any { it.category == TimerStageCategory.BAKING })

        val zojirushi = PreloadedTimerRoutines.ZOJIRUSHI_WORKFLOW
        assertTrue(zojirushi.stages.any { it.category == TimerStageCategory.PREHEAT })
        assertTrue(zojirushi.stages.any { it.category == TimerStageCategory.KNEADING })
    }
}
