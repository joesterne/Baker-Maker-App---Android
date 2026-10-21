package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BakingStage
import com.example.data.model.BakingStageType
import com.example.data.model.BreadMachineCycle
import com.example.data.model.PreloadedBreadMachineCycles

/**
 * Room Entity that persists and tracks multi-stage bread machine cycles,
 * stage-by-stage progression, and dedicated 'add-ins' (raisins, nuts, seeds) alarms.
 */
@Entity(tableName = "cycle_tracking")
data class BreadMachineCycleTrackerEntity(
    @PrimaryKey
    val cycleId: String = DEFAULT_TRACKER_ID,
    val cycleName: String,
    val shortName: String,
    val courseNumber: Int,
    val courseName: String,
    val description: String,
    val emoji: String,
    val totalMinutes: Int,
    val status: String = STATUS_IDLE, // "IDLE", "RUNNING", "PAUSED", "COMPLETED"
    val currentStageIndex: Int = 0,
    val stageElapsedSeconds: Int = 0,
    val stageDurationSeconds: Int = 0,
    val totalElapsedSeconds: Int = 0,
    val totalDurationSeconds: Int = 0,
    val stages: List<BakingStage> = emptyList(),

    // Add-in alarm configuration & state (e.g. raisins, nuts, seeds, chocolate chips)
    val hasAddInAlarm: Boolean = true,
    val addInStageIndex: Int = 3, // Typically Knead 2 (twin-blade vigorous mix)
    val addInStageName: String = "Knead 2 (Twin-Blade Vigorous Mix)",
    val addInItems: List<String> = DEFAULT_ADD_IN_ITEMS,
    val addInMinuteMark: Int = 15, // Chime sounds at 15 minutes into Knead 2
    val addInAlarmTriggered: Boolean = false,
    val addInAlarmAcknowledged: Boolean = false,
    val addInChimeSound: Boolean = true,
    val addInVibrate: Boolean = true,
    val simulationSpeed: Int = 1,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
) {
    val currentStage: BakingStage?
        get() = stages.getOrNull(currentStageIndex)

    val overallProgressFraction: Float
        get() = if (totalDurationSeconds > 0) {
            (totalElapsedSeconds.toFloat() / totalDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val stageProgressFraction: Float
        get() = if (stageDurationSeconds > 0) {
            (stageElapsedSeconds.toFloat() / stageDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val remainingTotalSeconds: Int
        get() = (totalDurationSeconds - totalElapsedSeconds).coerceAtLeast(0)

    val remainingStageSeconds: Int
        get() = (stageDurationSeconds - stageElapsedSeconds).coerceAtLeast(0)

    val isRunning: Boolean
        get() = status == STATUS_RUNNING

    val isPaused: Boolean
        get() = status == STATUS_PAUSED

    val isCompleted: Boolean
        get() = status == STATUS_COMPLETED

    companion object {
        const val DEFAULT_TRACKER_ID = "active_cycle_tracker"
        const val STATUS_IDLE = "IDLE"
        const val STATUS_RUNNING = "RUNNING"
        const val STATUS_PAUSED = "PAUSED"
        const val STATUS_COMPLETED = "COMPLETED"

        val DEFAULT_ADD_IN_ITEMS = listOf(
            "Sun-Maid Thompson Raisins (80g)",
            "Toasted Chopped Walnuts (50g)",
            "Dried Tart Cranberries (40g)",
            "Roasted Pumpkin & Sunflower Seeds (30g)"
        )

        fun fromCycle(
            cycle: BreadMachineCycle = PreloadedBreadMachineCycles.WHITE_BREAD,
            cycleId: String = DEFAULT_TRACKER_ID,
            customAddIns: List<String>? = null
        ): BreadMachineCycleTrackerEntity {
            val stages = cycle.stages
            val totalSecs = cycle.totalDurationSeconds
            val firstStage = stages.firstOrNull()
            val firstStageSecs = (firstStage?.durationMinutes ?: 0) * 60

            // Locate add-in stage (usually Knead 2 or stage with hasIngredientAlert)
            val addInIdx = stages.indexOfFirst {
                it.hasIngredientAlert || it.type == BakingStageType.KNEAD_2
            }.let { if (it >= 0) it else 3.coerceAtMost(stages.size - 1) }

            val addInStage = stages.getOrNull(addInIdx)

            return BreadMachineCycleTrackerEntity(
                cycleId = cycleId,
                cycleName = cycle.name,
                shortName = cycle.shortName,
                courseNumber = cycle.courseNumber,
                courseName = cycle.course.name,
                description = cycle.description,
                emoji = cycle.emoji,
                totalMinutes = cycle.totalMinutes,
                status = STATUS_IDLE,
                currentStageIndex = 0,
                stageElapsedSeconds = 0,
                stageDurationSeconds = firstStageSecs,
                totalElapsedSeconds = 0,
                totalDurationSeconds = totalSecs,
                stages = stages,
                hasAddInAlarm = true,
                addInStageIndex = addInIdx,
                addInStageName = addInStage?.title ?: "Knead 2 (Add-Ins Chime)",
                addInItems = customAddIns ?: DEFAULT_ADD_IN_ITEMS,
                addInMinuteMark = 15,
                addInAlarmTriggered = false,
                addInAlarmAcknowledged = false,
                addInChimeSound = true,
                addInVibrate = true,
                simulationSpeed = 1,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
        }
    }
}
