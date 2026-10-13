package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BakingStage
import com.example.data.model.BakingStageType
import com.example.data.model.BreadMachineCycle
import com.example.data.model.PreloadedBreadMachineCycles
import com.example.ui.theme.AlertBerryRed
import com.example.ui.theme.BakeFireOrange
import com.example.ui.theme.BreadAmber
import com.example.ui.theme.BreadCrustDark
import com.example.ui.theme.BreadCrustGolden
import com.example.ui.theme.BreadWarmWheat
import com.example.ui.theme.KneadGreen
import com.example.ui.theme.ProofingBlue
import kotlinx.coroutines.delay

/**
 * Composable screen that displays a countdown timer for different bread machine cycles
 * with multi-layered visual progress indicators, stage-by-stage progress tracking,
 * hardware telemetry (temperature/motor RPM), alert chimes, and interactive controls.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BreadMachineCyclesTimerScreen(
    initialCycle: BreadMachineCycle = PreloadedBreadMachineCycles.WHITE_BREAD,
    onLoadCycleIntoBaker: ((BreadMachineCycle) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Current selected bread machine cycle
    var selectedCycle by remember { mutableStateOf(initialCycle) }

    // Timer runtime state
    var isRunning by remember { mutableStateOf(false) }
    var currentStageIndex by remember { mutableIntStateOf(0) }
    var stageElapsedSeconds by remember { mutableIntStateOf(0) }
    var totalElapsedSeconds by remember { mutableIntStateOf(0) }
    var simulationSpeed by remember { mutableIntStateOf(1) } // 1x, 10x, 30x, 60x

    // Active alert dialog state
    var activeAlertMessage by remember { mutableStateOf<String?>(null) }
    var activeAlertTitle by remember { mutableStateOf<String?>(null) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    val stages = selectedCycle.stages
    val activeStage = stages.getOrNull(currentStageIndex) ?: stages.first()
    val activeStageTotalSeconds = activeStage.durationMinutes * 60

    // Calculate progress fractions
    val totalCycleDurationSeconds = selectedCycle.totalDurationSeconds
    val overallProgressFraction = if (totalCycleDurationSeconds > 0) {
        (totalElapsedSeconds.toFloat() / totalCycleDurationSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val stageProgressFraction = if (activeStageTotalSeconds > 0) {
        (stageElapsedSeconds.toFloat() / activeStageTotalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val stageRemainingSeconds = (activeStageTotalSeconds - stageElapsedSeconds).coerceAtLeast(0)
    val totalRemainingSeconds = (totalCycleDurationSeconds - totalElapsedSeconds).coerceAtLeast(0)

    // Animated progress bars for silky smooth visuals
    val animatedOverallProgress by animateFloatAsState(
        targetValue = overallProgressFraction,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "overall_progress"
    )
    val animatedStageProgress by animateFloatAsState(
        targetValue = stageProgressFraction,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "stage_progress"
    )

    // Autonomous Ticker Loop (respects simulation speed without flooding Compose)
    LaunchedEffect(isRunning, simulationSpeed, currentStageIndex) {
        while (isRunning) {
            val stepDelay = if (simulationSpeed > 10) 250L else (1000L / simulationSpeed.coerceAtLeast(1))
            val stepDelta = if (simulationSpeed > 10) (simulationSpeed / 4).coerceAtLeast(1) else 1
            delay(stepDelay)
            val currentStageSecs = activeStage.durationMinutes * 60
            if (stageElapsedSeconds + stepDelta >= currentStageSecs) {
                // Stage finished! Check for alerts or advance
                if (activeStage.hasIngredientAlert && activeStage.alertTitle != null) {
                    activeAlertTitle = "🔔 ${activeStage.alertTitle}"
                    activeAlertMessage = activeStage.alertDescription ?: "Event occurred in stage: ${activeStage.title}"
                }

                val nextIndex = currentStageIndex + 1
                if (nextIndex < stages.size) {
                    currentStageIndex = nextIndex
                    stageElapsedSeconds = 0
                    totalElapsedSeconds += stepDelta
                } else {
                    // Full cycle complete!
                    isRunning = false
                    totalElapsedSeconds = totalCycleDurationSeconds
                    stageElapsedSeconds = currentStageSecs
                    showCompletionDialog = true
                    break
                }
            } else {
                stageElapsedSeconds += stepDelta
                totalElapsedSeconds = (totalElapsedSeconds + stepDelta).coerceAtMost(totalCycleDurationSeconds)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("bread_machine_cycles_timer_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header & Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bread Machine Cycles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Automated stage timers & visual progress monitors",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Speed multiplier selector
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.testTag("simulation_speed_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Simulation Speed",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${simulationSpeed}x",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Cycle Selector Carousel
        Column {
            Text(
                text = "Select Machine Cycle Course (${PreloadedBreadMachineCycles.ALL_CYCLES.size} Available):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PreloadedBreadMachineCycles.ALL_CYCLES.forEach { cycle ->
                    val isSelected = cycle.id == selectedCycle.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (cycle.id != selectedCycle.id) {
                                selectedCycle = cycle
                                isRunning = false
                                currentStageIndex = 0
                                stageElapsedSeconds = 0
                                totalElapsedSeconds = 0
                            }
                        },
                        leadingIcon = {
                            Text(text = cycle.emoji, fontSize = 14.sp)
                        },
                        label = {
                            Column {
                                Text(
                                    text = "Course ${cycle.courseNumber} • ${cycle.shortName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "${cycle.totalMinutes / 60}h ${cycle.totalMinutes % 60}m",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("cycle_chip_${cycle.id}")
                    )
                }
            }
        }

        // Active Cycle Banner Card
        ActiveCycleOverviewCard(
            cycle = selectedCycle,
            currentStageIndex = currentStageIndex,
            totalStages = stages.size,
            isRunning = isRunning,
            totalRemainingSeconds = totalRemainingSeconds,
            totalDurationSeconds = totalCycleDurationSeconds,
            onLoadIntoBaker = onLoadCycleIntoBaker
        )

        // Visual Progress Indicator #1: Dual-Ring Circular Hero Countdown Meter
        DualRingCountdownHeroCard(
            cycle = selectedCycle,
            activeStage = activeStage,
            stepNumber = currentStageIndex + 1,
            totalSteps = stages.size,
            overallProgress = animatedOverallProgress,
            stageProgress = animatedStageProgress,
            stageRemainingSeconds = stageRemainingSeconds,
            stageTotalSeconds = activeStageTotalSeconds,
            totalRemainingSeconds = totalRemainingSeconds,
            isRunning = isRunning,
            onStart = { isRunning = true },
            onPause = { isRunning = false },
            onResume = { isRunning = true },
            onReset = {
                isRunning = false
                currentStageIndex = 0
                stageElapsedSeconds = 0
                totalElapsedSeconds = 0
            },
            onSkipStage = {
                if (currentStageIndex < stages.size - 1) {
                    val skippedStageSecs = activeStage.durationMinutes * 60
                    totalElapsedSeconds = (totalElapsedSeconds + (skippedStageSecs - stageElapsedSeconds)).coerceAtMost(totalCycleDurationSeconds)
                    currentStageIndex += 1
                    stageElapsedSeconds = 0
                }
            },
            onPrevStage = {
                if (currentStageIndex > 0) {
                    currentStageIndex -= 1
                    stageElapsedSeconds = 0
                }
            },
            onAdjustSeconds = { delta ->
                val newStageElapsed = (stageElapsedSeconds - delta).coerceAtLeast(0)
                stageElapsedSeconds = newStageElapsed
            },
            canSkip = currentStageIndex < stages.size - 1,
            canPrev = currentStageIndex > 0
        )

        // Visual Progress Indicator #2: Horizontal Multi-Segment Cycle Progress Bar
        SegmentedCycleProgressBar(
            stages = stages,
            currentStageIndex = currentStageIndex,
            stageProgressFraction = animatedStageProgress,
            onStageClick = { idx ->
                currentStageIndex = idx
                stageElapsedSeconds = 0
            }
        )

        // Visual Progress Indicator #3: Active Stage Telemetry & Hardware Gauges
        ActiveStageHardwareTelemetryCard(
            stage = activeStage,
            selectedCycle = selectedCycle
        )

        // Visual Progress Indicator #4: Step-by-Step Interactive Timeline
        CycleStagesTimelineCard(
            stages = stages,
            currentStageIndex = currentStageIndex,
            activeStageProgress = animatedStageProgress,
            onSelectStage = { idx ->
                currentStageIndex = idx
                stageElapsedSeconds = 0
            }
        )

        // Simulation Speed & Quick Adjustments Footer
        SimulationSpeedControllerCard(
            currentSpeed = simulationSpeed,
            onSetSpeed = { simulationSpeed = it },
            onFastDemo = {
                simulationSpeed = 30
                isRunning = true
            }
        )
    }

    // Alert Dialogs
    if (activeAlertTitle != null && activeAlertMessage != null) {
        AlertDialog(
            onDismissRequest = {
                activeAlertTitle = null
                activeAlertMessage = null
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = BakeFireOrange,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(text = activeAlertTitle!!, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = activeAlertMessage!!)
            },
            confirmButton = {
                Button(
                    onClick = {
                        activeAlertTitle = null
                        activeAlertMessage = null
                    }
                ) {
                    Text("Acknowledge & Continue")
                }
            }
        )
    }

    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = KneadGreen,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text("🎉 ${selectedCycle.name} Complete!", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("The baking cycle has fully finished.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Unplug machine and open lid carefully.\n" +
                                "• Wear oven mitts to unlock and remove hot bread pan.\n" +
                                "• Invert pan over cooling rack to release loaf.\n" +
                                "• Allow loaf to cool for at least 30-45 minutes before slicing.\n\n" +
                                "📸 Pro Tip: Snap a photo of your completed loaf in your recipe library to track your golden crust and crumb spring!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showCompletionDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

/**
 * Overview card displaying the selected bread machine cycle details.
 */
@Composable
private fun ActiveCycleOverviewCard(
    cycle: BreadMachineCycle,
    currentStageIndex: Int,
    totalStages: Int,
    isRunning: Boolean,
    totalRemainingSeconds: Int,
    totalDurationSeconds: Int,
    onLoadIntoBaker: ((BreadMachineCycle) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_cycle_overview_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = cycle.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = cycle.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Course #${cycle.courseNumber} • ${cycle.totalMinutes / 60}h ${cycle.totalMinutes % 60}m Total Cycle",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Live Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isRunning) KneadGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isRunning) KneadGreen else MaterialTheme.colorScheme.outline)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRunning) "RUNNING" else "PAUSED / IDLE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isRunning) KneadGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = cycle.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (cycle.tips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BreadCrustGolden,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cycle.tips,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (onLoadIntoBaker != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { onLoadIntoBaker(cycle) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("load_cycle_into_baker_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Load Course #${cycle.courseNumber} into Baker Pan")
                }
            }
        }
    }
}

/**
 * Visual Progress Indicator #1: Dual-Ring Circular Hero Countdown Meter
 * Outer ring tracks Overall Cycle Progress (0-100%).
 * Inner ring tracks Active Stage Progress (0-100%).
 * Center displays massive digital countdown time and stage information.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DualRingCountdownHeroCard(
    cycle: BreadMachineCycle,
    activeStage: BakingStage,
    stepNumber: Int,
    totalSteps: Int,
    overallProgress: Float,
    stageProgress: Float,
    stageRemainingSeconds: Int,
    stageTotalSeconds: Int,
    totalRemainingSeconds: Int,
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onSkipStage: () -> Unit,
    onPrevStage: () -> Unit,
    onAdjustSeconds: (Int) -> Unit,
    canSkip: Boolean,
    canPrev: Boolean,
    modifier: Modifier = Modifier
) {
    val stageColor = when (activeStage.type) {
        BakingStageType.PREHEAT -> BreadAmber
        BakingStageType.KNEAD_1, BakingStageType.KNEAD_2 -> BreadCrustGolden
        BakingStageType.REST_AUTOLYSE -> MaterialTheme.colorScheme.tertiary
        BakingStageType.RISE_1, BakingStageType.RISE_2, BakingStageType.RISE_3 -> ProofingBlue
        BakingStageType.PUNCH_DOWN -> KneadGreen
        BakingStageType.BAKE -> BakeFireOrange
        BakingStageType.KEEP_WARM -> BreadWarmWheat
    }

    val stageMins = stageRemainingSeconds / 60
    val stageSecs = stageRemainingSeconds % 60
    val stageTimeFormatted = com.example.ui.util.FastTimeFormatter.formatMmSs(stageMins, stageSecs)

    val totalTimeFormatted = com.example.ui.util.FastTimeFormatter.formatSeconds(totalRemainingSeconds)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dual_ring_countdown_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, stageColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Stage Step Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = stageColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "STEP $stepNumber OF $totalSteps • ${activeStage.type.displayName.uppercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = stageColor,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = activeStage.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Dual-Ring Concentric Circular Progress Meters
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .testTag("dual_ring_progress_indicator")
            ) {
                // 1. Outer Ring: Total Cycle Progress Track
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(230.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    strokeWidth = 10.dp
                )
                CircularProgressIndicator(
                    progress = { overallProgress },
                    modifier = Modifier.size(230.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round
                )

                // 2. Inner Ring: Active Stage Progress Track
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(196.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    strokeWidth = 12.dp
                )
                CircularProgressIndicator(
                    progress = { stageProgress },
                    modifier = Modifier.size(196.dp),
                    color = stageColor,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )

                // Center Information Display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "STAGE TIME REMAINING",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Large Monospace Stage Timer
                    Text(
                        text = stageTimeFormatted,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        fontSize = 38.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (stageRemainingSeconds <= 60 && isRunning) AlertBerryRed else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("stage_countdown_clock")
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Percentage badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = stageColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Stage ${(stageProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = stageColor,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Cycle ${(overallProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Cycle Left: $totalTimeFormatted",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Adjustment Pills (-5m, -1m, +1m, +5m)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf(-300 to "-5m", -60 to "-1m", 60 to "+1m", 300 to "+5m").forEach { (delta, label) ->
                    OutlinedButton(
                        onClick = { onAdjustSeconds(delta) },
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(32.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Control Buttons (Prev, Play/Pause, Next, Reset)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Stage
                IconButton(
                    onClick = onPrevStage,
                    enabled = canPrev,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (canPrev) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .testTag("cycle_prev_stage_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Previous Stage",
                        tint = if (canPrev) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }

                // Reset Button
                IconButton(
                    onClick = onReset,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("cycle_reset_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Main Play / Pause Hero Button
                Button(
                    onClick = if (isRunning) onPause else onStart,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) BakeFireOrange else stageColor
                    ),
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("cycle_play_pause_btn"),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start Countdown",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Skip to Next Stage
                IconButton(
                    onClick = onSkipStage,
                    enabled = canSkip,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (canSkip) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .testTag("cycle_next_stage_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Next Stage",
                        tint = if (canSkip) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

/**
 * Visual Progress Indicator #2: Horizontal Multi-Segment Cycle Progress Bar
 * Displays every stage segment proportionally with completion checkmarks,
 * active stage progress animation, and click-to-jump support.
 */
@Composable
private fun SegmentedCycleProgressBar(
    stages: List<BakingStage>,
    currentStageIndex: Int,
    stageProgressFraction: Float,
    onStageClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("segmented_cycle_progress_bar"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cycle Phase Progress Tracker",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap stage to inspect",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-segment progress bar row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                stages.forEachIndexed { index, stage ->
                    val isCompleted = index < currentStageIndex
                    val isCurrent = index == currentStageIndex
                    val segmentWeight = stage.durationMinutes.coerceAtLeast(3).toFloat()

                    val segmentColor = when (stage.type) {
                        BakingStageType.PREHEAT -> BreadAmber
                        BakingStageType.KNEAD_1, BakingStageType.KNEAD_2 -> BreadCrustGolden
                        BakingStageType.REST_AUTOLYSE -> MaterialTheme.colorScheme.tertiary
                        BakingStageType.RISE_1, BakingStageType.RISE_2, BakingStageType.RISE_3 -> ProofingBlue
                        BakingStageType.PUNCH_DOWN -> KneadGreen
                        BakingStageType.BAKE -> BakeFireOrange
                        BakingStageType.KEEP_WARM -> BreadWarmWheat
                    }

                    Box(
                        modifier = Modifier
                            .weight(segmentWeight)
                            .fillMaxSize()
                            .clickable { onStageClick(index) }
                            .background(
                                when {
                                    isCompleted -> segmentColor
                                    isCurrent -> segmentColor.copy(alpha = 0.3f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // If active, show progress fill
                        if (isCurrent) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(stageProgressFraction)
                                    .fillMaxSize()
                                    .background(segmentColor)
                            )
                        }

                        // Short Code Label inside segment
                        Text(
                            text = stage.type.shortCode,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted || isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legend indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendChip("PRE", "Preheat", BreadAmber)
                LegendChip("KN", "Knead", BreadCrustGolden)
                LegendChip("RS", "Proof", ProofingBlue)
                LegendChip("BAK", "Bake", BakeFireOrange)
            }
        }
    }
}

@Composable
private fun LegendChip(code: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$code: $label",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Visual Progress Indicator #3: Hardware telemetry gauges
 * Displays live chamber temperature, motor RPM, and heating element status.
 */
@Composable
private fun ActiveStageHardwareTelemetryCard(
    stage: BakingStage,
    selectedCycle: BreadMachineCycle
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stage_hardware_telemetry_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Machine Hardware Operations & Telemetry",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Real-time heating element wattage & motor drives",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Chamber Temperature Gauge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = if (stage.targetTempF > 150) BakeFireOrange else ProofingBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Target Temp",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stage.targetTempF}°F",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (stage.targetTempF > 150) BakeFireOrange else ProofingBlue
                        )
                        Text(
                            text = if (stage.targetTempF > 300) "Hearth baking mode" else "Fermentation proof",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Dual-Blade Motor Speed Gauge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = BreadCrustGolden,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Twin Motors",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (stage.motorRpm > 0) "${stage.motorRpm} RPM" else "REST / 0",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (stage.motorRpm > 0) BreadCrustGolden else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (stage.motorRpm > 0) "Dual counter-rotating" else "Motor stationary",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Heating Element Wattage & Lid Element Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = null,
                        tint = BakeFireOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Heating Elements: Base ${stage.heatingPowerPercent}% Power" +
                                if (stage.type == BakingStageType.BAKE && selectedCycle.hasLidHeater) " • Upper Lid Radiant Active" else "",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Visual Progress Indicator #4: Step-by-Step Interactive Timeline
 * Scrollable list of every stage in the bread machine cycle with linear progress bars.
 */
@Composable
private fun CycleStagesTimelineCard(
    stages: List<BakingStage>,
    currentStageIndex: Int,
    activeStageProgress: Float,
    onSelectStage: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cycle_stages_timeline_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Cycle Stages Timeline (${stages.size} Steps)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Detailed sequence of automatic machine phases",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            stages.forEachIndexed { index, stage ->
                val isCompleted = index < currentStageIndex
                val isCurrent = index == currentStageIndex

                val stageColor = when (stage.type) {
                    BakingStageType.PREHEAT -> BreadAmber
                    BakingStageType.KNEAD_1, BakingStageType.KNEAD_2 -> BreadCrustGolden
                    BakingStageType.REST_AUTOLYSE -> MaterialTheme.colorScheme.tertiary
                    BakingStageType.RISE_1, BakingStageType.RISE_2, BakingStageType.RISE_3 -> ProofingBlue
                    BakingStageType.PUNCH_DOWN -> KneadGreen
                    BakingStageType.BAKE -> BakeFireOrange
                    BakingStageType.KEEP_WARM -> BreadWarmWheat
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCurrent) stageColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isCurrent) 1.5.dp else 1.dp,
                        color = if (isCurrent) stageColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectStage(index) }
                        .testTag("timeline_stage_$index")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Step status indicator circle
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCompleted -> KneadGreen
                                                isCurrent -> stageColor
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = stage.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${stage.type.displayName} • ${stage.targetTempF}°F" +
                                                if (stage.motorRpm > 0) " • ${stage.motorRpm} RPM" else "",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Stage Duration badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = stageColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${stage.durationMinutes} min",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = stageColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Linear progress bar for the active stage
                        if (isCurrent) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { activeStageProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = stageColor,
                                trackColor = stageColor.copy(alpha = 0.2f)
                            )
                        }

                        // Alerts or special events in this stage
                        if (stage.hasIngredientAlert && stage.alertTitle != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = BakeFireOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stage.alertTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = BakeFireOrange,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                if (index < stages.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Controller for testing countdown timers rapidly using simulation speeds.
 */
@Composable
private fun SimulationSpeedControllerCard(
    currentSpeed: Int,
    onSetSpeed: (Int) -> Unit,
    onFastDemo: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("simulation_speed_controller_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Countdown Demonstration Speed",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onFastDemo,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("fast_demo_btn")
                ) {
                    Text("Fast Demo (30x)", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Simulate multi-hour bread cycles in seconds to observe visual progress indicators animating across stages.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1 to "1x Real-Time", 5 to "5x Speed", 15 to "15x Fast", 30 to "30x Turbo", 60 to "60x Max").forEach { (speed, label) ->
                    val isSelected = currentSpeed == speed
                    OutlinedButton(
                        onClick = { onSetSpeed(speed) },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
                        )
                    ) {
                        Text(
                            text = label.substringBefore(" "),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
