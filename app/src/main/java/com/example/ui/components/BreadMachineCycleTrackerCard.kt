package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.local.BreadMachineCycleTrackerEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dedicated Room-backed UI component that tracks and displays a multi-stage bread machine cycle,
 * with real-time progression, stage timeline visualizer, and dedicated alarms for 'add-ins'
 * like raisins, chopped nuts, cranberries, and seeds.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BreadMachineCycleTrackerCard(
    tracker: BreadMachineCycleTrackerEntity?,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onSkipStage: () -> Unit,
    onJumpToStage: (Int) -> Unit,
    onSelectCycle: (BreadMachineCycle) -> Unit,
    onSetSpeed: (Int) -> Unit,
    onAcknowledgeAlarm: () -> Unit,
    onTestAddInAlarm: () -> Unit,
    onUpdateAddInItems: (List<String>) -> Unit,
    onToggleChimeSound: (Boolean) -> Unit,
    onToggleVibrate: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tracker == null) return

    val currentStage = tracker.currentStage
    val stages = tracker.stages
    var showEditAddInsDialog by remember { mutableStateOf(false) }

    val animatedOverallProgress by animateFloatAsState(
        targetValue = tracker.overallProgressFraction,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "overall_progress_anim"
    )

    val animatedStageProgress by animateFloatAsState(
        targetValue = tracker.stageProgressFraction,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "stage_progress_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cycle_tracker_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row: Room Database Badge & Status Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Database persistence badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.testTag("cycle_tracker_room_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Room DB",
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "ROOM DB: cycle_tracking",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Cycle Running / Paused / Idle Status Chip
                val statusColor by animateColorAsState(
                    targetValue = when (tracker.status) {
                        BreadMachineCycleTrackerEntity.STATUS_RUNNING -> KneadGreen
                        BreadMachineCycleTrackerEntity.STATUS_PAUSED -> BreadAmber
                        BreadMachineCycleTrackerEntity.STATUS_COMPLETED -> BreadCrustGolden
                        else -> MaterialTheme.colorScheme.outline
                    },
                    label = "status_color"
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = tracker.status,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }

            // Cycle Title & Course Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${tracker.emoji} ${tracker.cycleName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Course #${tracker.courseNumber} (${tracker.courseName}) • Total ${tracker.totalMinutes} min (${formatHoursAndMinutes(tracker.totalMinutes)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Cycle Selector Chips
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Select Bread Machine Cycle:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PreloadedBreadMachineCycles.ALL_CYCLES.forEach { cycle ->
                        val isSelected = tracker.courseNumber == cycle.courseNumber
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCycle(cycle) },
                            label = { Text("${cycle.emoji} ${cycle.shortName}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BreadCrustGolden.copy(alpha = 0.2f),
                                selectedLabelColor = BreadCrustGolden
                            )
                        )
                    }
                }
            }

            // =========================================================================
            // DEDICATED ADD-INS ALARM & DISPENSER SECTION (Raisins, Nuts, Seeds)
            // =========================================================================
            AddInsAlarmDispenserSection(
                tracker = tracker,
                onAcknowledge = onAcknowledgeAlarm,
                onTestAlarm = onTestAddInAlarm,
                onEditAddIns = { showEditAddInsDialog = true },
                onToggleChime = onToggleChimeSound,
                onToggleVibrate = onToggleVibrate
            )

            // Overall Cycle Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OVERALL CYCLE PROGRESS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${(animatedOverallProgress * 100).toInt()}% • ${formatSecondsToTime(tracker.remainingTotalSeconds)} left",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedOverallProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = BreadCrustGolden,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }

            // Current Active Stage Detail Banner
            currentStage?.let { stage ->
                CurrentStageDetailCard(
                    stage = stage,
                    stageIndex = tracker.currentStageIndex,
                    totalStages = stages.size,
                    remainingSeconds = tracker.remainingStageSeconds,
                    durationSeconds = tracker.stageDurationSeconds,
                    progress = animatedStageProgress
                )
            }

            // Multi-Stage Visual Stepper Timeline
            MultiStageStepperTimeline(
                stages = stages,
                currentStageIndex = tracker.currentStageIndex,
                addInStageIndex = tracker.addInStageIndex,
                onJumpToStage = onJumpToStage
            )

            // Bottom Controls Bar: Play/Pause/Resume, Reset, Next Stage, Speed
            CycleTrackerControlBar(
                status = tracker.status,
                simulationSpeed = tracker.simulationSpeed,
                onStart = onStart,
                onPause = onPause,
                onResume = onResume,
                onReset = onReset,
                onSkipStage = onSkipStage,
                onSetSpeed = onSetSpeed
            )
        }
    }

    // Modal Dialog: Customize Add-in Ingredients (Raisins, Walnuts, etc.)
    if (showEditAddInsDialog) {
        CustomizeAddInsDialog(
            currentItems = tracker.addInItems,
            onDismiss = { showEditAddInsDialog = false },
            onSave = { updatedList ->
                onUpdateAddInItems(updatedList)
                showEditAddInsDialog = false
            }
        )
    }
}

/**
 * Dedicated Add-Ins Alarm & Dispenser Section for raisins, nuts, and seeds.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddInsAlarmDispenserSection(
    tracker: BreadMachineCycleTrackerEntity,
    onAcknowledge: () -> Unit,
    onTestAlarm: () -> Unit,
    onEditAddIns: () -> Unit,
    onToggleChime: (Boolean) -> Unit,
    onToggleVibrate: (Boolean) -> Unit
) {
    val isAlarmActive = tracker.addInAlarmTriggered
    val isAcknowledged = tracker.addInAlarmAcknowledged

    val containerBg = when {
        isAlarmActive -> AlertBerryRed.copy(alpha = 0.12f)
        isAcknowledged -> KneadGreen.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    val borderColor = when {
        isAlarmActive -> AlertBerryRed
        isAcknowledged -> KneadGreen.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cycle_tracker_add_in_alarm_banner"),
        shape = RoundedCornerShape(18.dp),
        color = containerBg,
        border = androidx.compose.foundation.BorderStroke(if (isAlarmActive) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header with Alert Icon & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isAlarmActive) Icons.Filled.NotificationsActive else Icons.Filled.Alarm,
                        contentDescription = "Add-Ins Alarm",
                        tint = if (isAlarmActive) AlertBerryRed else if (isAcknowledged) KneadGreen else BreadCrustGolden,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = if (isAlarmActive) "🚨 ADD-INS DISPENSER ALARM ACTIVE!" else "Add-Ins Chime & Mix-Ins",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isAlarmActive) AlertBerryRed else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when {
                                isAlarmActive -> "Open dispenser / lid and add your raisins, nuts, or seeds now!"
                                isAcknowledged -> "✓ Add-ins added and acknowledged for this bake"
                                else -> "Scheduled at ${tracker.addInStageName} (approx. ${tracker.addInMinuteMark}m into knead)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isAlarmActive) AlertBerryRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // If alarm is active, show pulsating badge
                if (isAlarmActive) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AlertBerryRed
                    ) {
                        Text(
                            text = "RINGING",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Mix-in items list (raisins, nuts, seeds)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Prepared Add-Ins (${tracker.addInItems.size}):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick = onEditAddIns,
                        modifier = Modifier.testTag("cycle_tracker_customize_addins_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Mix-ins",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Customize", fontSize = 12.sp)
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tracker.addInItems.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Alarm Action Buttons
            if (isAlarmActive) {
                Button(
                    onClick = onAcknowledge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cycle_tracker_acknowledge_alarm_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AlertBerryRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("✓ Add Ingredients Added & Acknowledge (Mute Chime)", fontWeight = FontWeight.Bold)
                }
            }

            // Notification preferences & Test Trigger Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Audible Chime toggle chip
                    FilterChip(
                        selected = tracker.addInChimeSound,
                        onClick = { onToggleChime(!tracker.addInChimeSound) },
                        label = { Text("Chime Beep", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                    // Haptic Vibrate toggle chip
                    FilterChip(
                        selected = tracker.addInVibrate,
                        onClick = { onToggleVibrate(!tracker.addInVibrate) },
                        label = { Text("Vibrate", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }

                // Instant Test Alarm button
                OutlinedButton(
                    onClick = onTestAlarm,
                    modifier = Modifier.testTag("cycle_tracker_test_alarm_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Alarm", fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * Current Active Stage Detail Card.
 */
@Composable
private fun CurrentStageDetailCard(
    stage: BakingStage,
    stageIndex: Int,
    totalStages: Int,
    remainingSeconds: Int,
    durationSeconds: Int,
    progress: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "STAGE ${stageIndex + 1} OF $totalStages",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = stage.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Stage remaining countdown in large digital style
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatSecondsToTime(remainingSeconds),
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "of ${stage.durationMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = KneadGreen,
                trackColor = MaterialTheme.colorScheme.surface,
                strokeCap = StrokeCap.Round
            )

            // Stage Telemetry Preview: Temp & RPM
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = "Target Temp",
                        tint = BakeFireOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${stage.targetTempF}°F",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (stage.motorRpm > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Motor RPM",
                            tint = ProofingBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${stage.motorRpm} RPM (Knead)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (stage.hasIngredientAlert) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Chime",
                            tint = AlertBerryRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Add-Ins Chime Active",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AlertBerryRed
                        )
                    }
                }
            }
        }
    }
}

/**
 * Multi-Stage Stepper Timeline displaying each stage with interactive jumps.
 */
@Composable
private fun MultiStageStepperTimeline(
    stages: List<BakingStage>,
    currentStageIndex: Int,
    addInStageIndex: Int,
    onJumpToStage: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Multi-Stage Cycle Stages (Tap to Jump):",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stages.forEachIndexed { index, stage ->
                val isCompleted = index < currentStageIndex
                val isCurrent = index == currentStageIndex
                val isAddIn = index == addInStageIndex || stage.hasIngredientAlert

                val itemBg = when {
                    isCurrent -> BreadCrustGolden.copy(alpha = 0.2f)
                    isCompleted -> KneadGreen.copy(alpha = 0.12f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                }

                val borderStrokeColor = when {
                    isCurrent -> BreadCrustGolden
                    isCompleted -> KneadGreen.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = itemBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderStrokeColor),
                    modifier = Modifier
                        .clickable { onJumpToStage(index) }
                        .testTag("cycle_tracker_stage_item")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Stage Number / Checkmark
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCurrent -> BreadCrustGolden
                                        isCompleted -> KneadGreen
                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = stage.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCurrent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isAddIn) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = "Add-ins Chime",
                                        tint = AlertBerryRed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${stage.durationMinutes} min",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Control Bar for cycle play, pause, reset, speed, and skip.
 */
@Composable
private fun CycleTrackerControlBar(
    status: String,
    simulationSpeed: Int,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onSkipStage: () -> Unit,
    onSetSpeed: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (status) {
                BreadMachineCycleTrackerEntity.STATUS_RUNNING -> {
                    Button(
                        onClick = onPause,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cycle_tracker_pause_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = BreadAmber),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pause", fontWeight = FontWeight.Bold)
                    }
                }
                BreadMachineCycleTrackerEntity.STATUS_PAUSED -> {
                    Button(
                        onClick = onResume,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cycle_tracker_start_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = KneadGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Resume", fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cycle_tracker_start_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = BreadCrustGolden),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Cycle", fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedButton(
                onClick = onSkipStage,
                modifier = Modifier.testTag("cycle_tracker_skip_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.FastForward, contentDescription = "Next Stage", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Next")
            }

            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.testTag("cycle_tracker_reset_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
            }
        }

        // Simulation Speed Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Clock Speed:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(1, 10, 30, 60).forEach { speed ->
                    val isSelected = simulationSpeed == speed
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSetSpeed(speed) },
                        label = { Text("${speed}x", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BreadCrustGolden.copy(alpha = 0.2f),
                            selectedLabelColor = BreadCrustGolden
                        )
                    )
                }
            }
        }
    }
}

/**
 * Dialog to add, remove, or edit add-ins (raisins, nuts, seeds).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CustomizeAddInsDialog(
    currentItems: List<String>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    var itemsList by remember { mutableStateOf(currentItems.toMutableList()) }
    var newItemText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Alarm, contentDescription = null, tint = BreadCrustGolden)
                Text("Customize Add-Ins & Mix-Ins")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Specify the exact ingredients (e.g. raisins, toasted nuts, seeds) to chime for during Knead 2:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // List of current items with remove buttons
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    itemsList.forEachIndexed { index, item ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = item, style = MaterialTheme.typography.bodyMedium)
                                IconButton(
                                    onClick = {
                                        itemsList = itemsList.toMutableList().also { it.removeAt(index) }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = AlertBerryRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Add new item input field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newItemText,
                        onValueChange = { newItemText = it },
                        label = { Text("Add Item (e.g. Pecans)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (newItemText.isNotBlank()) {
                                itemsList = itemsList.toMutableList().also { it.add(newItemText.trim()) }
                                newItemText = ""
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add")
                    }
                }

                // Preset suggestions
                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Raisins (80g)", "Walnuts (50g)", "Cranberries (40g)", "Chocolate Chips (60g)", "Pecans (45g)", "Sunflower Seeds (30g)").forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.clickable {
                                if (!itemsList.contains(preset)) {
                                    itemsList = itemsList.toMutableList().also { it.add(preset) }
                                }
                            }
                        ) {
                            Text(
                                text = "+ $preset",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(itemsList) }
            ) {
                Text("Save to Room DB")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatSecondsToTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}

private fun formatHoursAndMinutes(totalMinutes: Int): String {
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
