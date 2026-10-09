package com.example.data.model

import java.util.UUID

enum class TimerStageCategory(
    val label: String,
    val description: String,
    val defaultMinutes: Int
) {
    AUTOLYSE("Autolyse & Rest", "Hydrate flour & water without salt/yeast", 45),
    KNEADING("Kneading & Mixing", "Develop gluten network & dough elasticity", 15),
    BULK_PROOF("Bulk Fermentation (Rise 1)", "First primary rise for flavor & gas volume", 90),
    STRETCH_FOLD("Stretch & Fold Interval", "Fold dough every 30 mins to build structure", 30),
    BENCH_REST("Bench Rest & Preshape", "Relax gluten before final shaping", 20),
    FINAL_PROOF("Final Proof (Rise 2 / 3)", "Final loaf rise in basket or bread pan", 60),
    PREHEAT("Preheat Oven / Chamber", "Get baking stone, cloche, or chamber to temp", 35),
    BAKING("Oven Baking", "High heat crust setting & oven spring", 45),
    COOLING("Cooling on Wire Rack", "Allow crumb gelatin to set before slicing", 60),
    CUSTOM("Custom Stage", "User-defined stage countdown", 30)
}

data class BreadStageTimer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: TimerStageCategory,
    val targetDurationSeconds: Int,
    val remainingSeconds: Int = targetDurationSeconds,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val bakerTips: String = "",
    val soundAlertEnabled: Boolean = true,
    val autoAdvanceNext: Boolean = false
) {
    val progressFraction: Float
        get() = if (targetDurationSeconds > 0) {
            1f - (remainingSeconds.toFloat() / targetDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val formattedRemaining: String
        get() {
            val total = remainingSeconds.coerceAtLeast(0)
            val hours = total / 3600
            val minutes = (total % 3600) / 60
            val seconds = total % 60
            return if (hours > 0) {
                String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
            }
        }

    val formattedTargetDuration: String
        get() {
            val hours = targetDurationSeconds / 3600
            val minutes = (targetDurationSeconds % 3600) / 60
            return if (hours > 0 && minutes > 0) {
                "${hours}h ${minutes}m"
            } else if (hours > 0) {
                "${hours}h"
            } else {
                "${minutes} min"
            }
        }
}

data class TimerRoutine(
    val id: String,
    val title: String,
    val description: String,
    val stages: List<BreadStageTimer>
)

object PreloadedTimerRoutines {

    val ARTISAN_SOURDOUGH = TimerRoutine(
        id = "artisan_sourdough_routine",
        title = "Artisan Wild Sourdough Schedule",
        description = "Complete 7-stage traditional wild levain schedule from autolyse to cooling.",
        stages = listOf(
            BreadStageTimer(
                id = "sd_autolyse",
                name = "Autolyse (Flour + Water)",
                category = TimerStageCategory.AUTOLYSE,
                targetDurationSeconds = 45 * 60,
                bakerTips = "Mix flour & water only. Rest lets enzymes break down starches and develops extensible gluten."
            ),
            BreadStageTimer(
                id = "sd_knead",
                name = "Incorporate Starter & Knead",
                category = TimerStageCategory.KNEADING,
                targetDurationSeconds = 15 * 60,
                bakerTips = "Add active starter & salt. Slap and fold or knead until dough leaves sides of bowl."
            ),
            BreadStageTimer(
                id = "sd_bulk_proof",
                name = "Bulk Fermentation (Rise 1)",
                category = TimerStageCategory.BULK_PROOF,
                targetDurationSeconds = 120 * 60,
                bakerTips = "Keep warm at 78°F. Look for 30-50% volume growth and visible aeration bubbles on surface."
            ),
            BreadStageTimer(
                id = "sd_bench_rest",
                name = "Bench Rest & Preshape",
                category = TimerStageCategory.BENCH_REST,
                targetDurationSeconds = 20 * 60,
                bakerTips = "Preshape into round boule. Let dough rest uncovered 20 minutes to relax tension."
            ),
            BreadStageTimer(
                id = "sd_final_proof",
                name = "Final Proof in Banneton",
                category = TimerStageCategory.FINAL_PROOF,
                targetDurationSeconds = 75 * 60,
                bakerTips = "Shape tightly with surface tension. Proof until poke test leaves gentle springback indent."
            ),
            BreadStageTimer(
                id = "sd_bake_steam",
                name = "Dutch Oven Bake (Steam / Lid On)",
                category = TimerStageCategory.BAKING,
                targetDurationSeconds = 25 * 60,
                bakerTips = "Score dough 1/2 inch deep with razor. Bake at 450°F with lid on to trap steam for maximum oven spring."
            ),
            BreadStageTimer(
                id = "sd_bake_crisp",
                name = "Crust Crisping (Lid Off)",
                category = TimerStageCategory.BAKING,
                targetDurationSeconds = 20 * 60,
                bakerTips = "Remove Dutch oven lid. Bake at 425°F until crust turns deep mahogany brown with blistered ears."
            ),
            BreadStageTimer(
                id = "sd_cooling",
                name = "Loaf Cooling on Wire Rack",
                category = TimerStageCategory.COOLING,
                targetDurationSeconds = 60 * 60,
                bakerTips = "Do NOT slice yet! Steam continues cooking the interior crumb. Slicing too early causes gummy crumb."
            )
        )
    )

    val QUICK_YEAST_BREAD = TimerRoutine(
        id = "quick_yeast_routine",
        title = "Same-Day Sandwich Bread Routine",
        description = "Streamlined 4-stage process for quick dinner rolls, milk bread, and sandwich loaves.",
        stages = listOf(
            BreadStageTimer(
                id = "qy_knead",
                name = "Primary Knead & Mixing",
                category = TimerStageCategory.KNEADING,
                targetDurationSeconds = 12 * 60,
                bakerTips = "Knead until dough passes windowpane test and feels smooth and supple."
            ),
            BreadStageTimer(
                id = "qy_first_proof",
                name = "First Rise (Bulk Proof)",
                category = TimerStageCategory.BULK_PROOF,
                targetDurationSeconds = 50 * 60,
                bakerTips = "Let rise in warm draft-free spot until doubled in volume."
            ),
            BreadStageTimer(
                id = "qy_final_proof",
                name = "Final Pan Proof (Rise 2)",
                category = TimerStageCategory.FINAL_PROOF,
                targetDurationSeconds = 40 * 60,
                bakerTips = "Degas, shape into cylinder, place in greased bread pan. Rise until 1 inch above pan rim."
            ),
            BreadStageTimer(
                id = "qy_bake",
                name = "Golden Sandwich Loaf Bake",
                category = TimerStageCategory.BAKING,
                targetDurationSeconds = 35 * 60,
                bakerTips = "Bake at 375°F. Internal loaf temperature should register 195°F–200°F."
            )
        )
    )

    val ZOJIRUSHI_WORKFLOW = TimerRoutine(
        id = "zojirushi_routine",
        title = "Zojirushi BB-PDC20 Machine Workflow",
        description = "Stage timing corresponding to Zojirushi Virtuoso Plus twin-blade automatic courses.",
        stages = listOf(
            BreadStageTimer(
                id = "zj_preheat",
                name = "Preheat & Temp Equalize",
                category = TimerStageCategory.PREHEAT,
                targetDurationSeconds = 25 * 60,
                bakerTips = "Sensors bring liquid, flour, and chamber to consistent 75°F before motor engages."
            ),
            BreadStageTimer(
                id = "zj_knead",
                name = "Twin Blade Kneading",
                category = TimerStageCategory.KNEADING,
                targetDurationSeconds = 22 * 60,
                bakerTips = "Twin clockwise & counter-clockwise blades thoroughly stretch gluten in horizontal pan."
            ),
            BreadStageTimer(
                id = "zj_rise1",
                name = "Rise 1 (Bulk Fermentation)",
                category = TimerStageCategory.BULK_PROOF,
                targetDurationSeconds = 45 * 60,
                bakerTips = "Chamber heating maintains 82°F optimal yeast incubation."
            ),
            BreadStageTimer(
                id = "zj_degas_paddle",
                name = "Paddle Removal & Punch Down",
                category = TimerStageCategory.BENCH_REST,
                targetDurationSeconds = 5 * 60,
                bakerTips = "Beep sounds: open lid and extract both kneading blades with hook to prevent holes in bottom."
            ),
            BreadStageTimer(
                id = "zj_final_rise",
                name = "Final Loaf Rise (Rise 3)",
                category = TimerStageCategory.FINAL_PROOF,
                targetDurationSeconds = 55 * 60,
                bakerTips = "Dough crowns smoothly across full length of 2-lb rectangular pan."
            ),
            BreadStageTimer(
                id = "zj_bake",
                name = "Dual Heater Artisan Bake",
                category = TimerStageCategory.BAKING,
                targetDurationSeconds = 55 * 60,
                bakerTips = "Base pan element and lid radiant element roast loaf simultaneously for uniform browning."
            )
        )
    )

    val ALL_ROUTINES = listOf(ARTISAN_SOURDOUGH, QUICK_YEAST_BREAD, ZOJIRUSHI_WORKFLOW)
}

data class StageTimersState(
    val activeRoutine: TimerRoutine = PreloadedTimerRoutines.ARTISAN_SOURDOUGH,
    val currentStageIndex: Int = 0,
    val isRunning: Boolean = false,
    val customStages: List<BreadStageTimer> = emptyList(),
    val completedAlert: BreadStageTimer? = null
) {
    val currentStage: BreadStageTimer?
        get() = activeRoutine.stages.getOrNull(currentStageIndex)

    val totalRoutineDurationSeconds: Int
        get() = activeRoutine.stages.sumOf { it.targetDurationSeconds }

    val totalRoutineRemainingSeconds: Int
        get() = activeRoutine.stages.sumOf { it.remainingSeconds }

    val overallRoutineProgress: Float
        get() = if (totalRoutineDurationSeconds > 0) {
            1f - (totalRoutineRemainingSeconds.toFloat() / totalRoutineDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f
}
