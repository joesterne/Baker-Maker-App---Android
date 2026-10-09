package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BreadStageTimer
import com.example.data.model.PreloadedTimerRoutines
import com.example.data.model.StageTimersState
import com.example.data.model.TimerRoutine
import com.example.data.model.TimerStageCategory
import com.example.ui.theme.AlertBerryRed
import com.example.ui.theme.BakeFireOrange
import com.example.ui.theme.BreadAmber
import com.example.ui.theme.BreadCrustGolden
import com.example.ui.theme.ProofingBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BreadStageTimerScreen(
    state: StageTimersState,
    onSelectRoutine: (TimerRoutine) -> Unit,
    onSelectStage: (Int) -> Unit,
    onStartCountdown: () -> Unit,
    onPauseCountdown: () -> Unit,
    onResumeCountdown: () -> Unit,
    onResetCountdown: () -> Unit,
    onAdjustCountdown: (Int) -> Unit,
    onSetStageDuration: (Int, Int) -> Unit,
    onSkipNextStage: () -> Unit,
    onPreviousStage: () -> Unit,
    onAddCustomStage: (String, TimerStageCategory, Int, String) -> Unit,
    onDeleteStage: (Int) -> Unit,
    onDismissAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeStage = state.currentStage
    var showAddStageDialog by remember { mutableStateOf(false) }
    var editingStageIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("bread_stage_timer_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Routine Selector Pills
        Column {
            Text(
                text = "Bread Baking Schedule Workflow",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PreloadedTimerRoutines.ALL_ROUTINES.forEach { routine ->
                    val isSelected = state.activeRoutine.id == routine.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectRoutine(routine) },
                        label = {
                            Text(
                                text = routine.title.substringBefore(" Schedule").substringBefore(" Routine").substringBefore(" Machine"),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        modifier = Modifier.testTag("routine_chip_${routine.id}")
                    )
                }
            }
        }

        // Active Stage Countdown Hero Card
        if (activeStage != null) {
            ActiveStageCountdownCard(
                stage = activeStage,
                stepNumber = state.currentStageIndex + 1,
                totalSteps = state.activeRoutine.stages.size,
                isRunning = state.isRunning,
                onStart = onStartCountdown,
                onPause = onPauseCountdown,
                onResume = onResumeCountdown,
                onReset = onResetCountdown,
                onAdjustSeconds = onAdjustCountdown,
                onSkipNext = onSkipNextStage,
                onPrevious = onPreviousStage,
                canGoPrevious = state.currentStageIndex > 0,
                canGoNext = state.currentStageIndex < state.activeRoutine.stages.size - 1,
                onEditDuration = { editingStageIndex = state.currentStageIndex }
            )
        }

        // Timeline of Stages in this Routine
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("timer_stages_timeline_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Stages Timeline (${state.activeRoutine.stages.size} Steps)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap a stage to jump or customize countdown",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showAddStageDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_custom_stage_btn"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Stage", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                state.activeRoutine.stages.forEachIndexed { index, stage ->
                    val isCurrent = index == state.currentStageIndex
                    StageTimelineItem(
                        stage = stage,
                        stepNumber = index + 1,
                        isCurrent = isCurrent,
                        isRoutineRunning = state.isRunning,
                        onClick = { onSelectStage(index) },
                        onEdit = { editingStageIndex = index },
                        onDelete = if (state.activeRoutine.stages.size > 1) {
                            { onDeleteStage(index) }
                        } else null
                    )
                    if (index < state.activeRoutine.stages.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Quick Stage Presets Card
        QuickPresetsCard(
            onQuickTimer = { name, category, minutes ->
                onAddCustomStage(name, category, minutes * 60, "Quick single-stage timer")
            }
        )
    }

    // Dialog for Step Completion
    state.completedAlert?.let { completedStage ->
        AlertDialog(
            onDismissRequest = onDismissAlert,
            icon = {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = BakeFireOrange,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "⏰ Stage Complete: ${completedStage.name}!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = "The countdown has finished for this step.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (completedStage.bakerTips.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Baker's Tip: ${completedStage.bakerTips}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismissAlert()
                        onStartCountdown()
                    }
                ) {
                    Text("Start Next Step")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissAlert) {
                    Text("Dismiss")
                }
            }
        )
    }

    // Dialog for Editing Stage Duration
    editingStageIndex?.let { stageIdx ->
        val stageToEdit = state.activeRoutine.stages.getOrNull(stageIdx)
        if (stageToEdit != null) {
            EditStageDurationDialog(
                stage = stageToEdit,
                onDismiss = { editingStageIndex = null },
                onSave = { newSeconds ->
                    onSetStageDuration(stageIdx, newSeconds)
                    editingStageIndex = null
                }
            )
        }
    }

    // Dialog for Adding Custom Stage
    if (showAddStageDialog) {
        AddCustomStageDialog(
            onDismiss = { showAddStageDialog = false },
            onAdd = { name, category, minutes, tips ->
                onAddCustomStage(name, category, minutes * 60, tips)
                showAddStageDialog = false
            }
        )
    }
}

/**
 * Big Hero Countdown Timer Card for the active stage
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveStageCountdownCard(
    stage: BreadStageTimer,
    stepNumber: Int,
    totalSteps: Int,
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onAdjustSeconds: (Int) -> Unit,
    onSkipNext: () -> Unit,
    onPrevious: () -> Unit,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onEditDuration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stageColor = when (stage.category) {
        TimerStageCategory.KNEADING -> BreadCrustGolden
        TimerStageCategory.BULK_PROOF, TimerStageCategory.FINAL_PROOF -> ProofingBlue
        TimerStageCategory.BAKING -> BakeFireOrange
        TimerStageCategory.AUTOLYSE, TimerStageCategory.BENCH_REST, TimerStageCategory.COOLING -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_stage_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, stageColor.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stage Header & Step Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = stageColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Step $stepNumber of $totalSteps • ${stage.category.label}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = stageColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onEditDuration,
                    modifier = Modifier.size(32.dp).testTag("edit_stage_duration_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Duration",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stage.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Circular Countdown Display
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(190.dp)
            ) {
                // Background Track
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    strokeWidth = 12.dp
                )
                // Active Countdown Progress
                CircularProgressIndicator(
                    progress = { stage.progressFraction },
                    modifier = Modifier.fillMaxSize(),
                    color = stageColor,
                    strokeWidth = 12.dp
                )

                // Digital Clock
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stage.formattedRemaining,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        fontSize = 38.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (stage.remainingSeconds <= 60 && isRunning) AlertBerryRed else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Target: ${stage.formattedTargetDuration}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (stage.isCompleted) {
                        Text(
                            text = "COMPLETED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Baker's Tip Banner
            if (stage.bakerTips.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = stageColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stage.bakerTips,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Incremental Adjustment Chips (-5m, -1m, +1m, +5m, +15m)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(-300 to "-5m", -60 to "-1m", 60 to "+1m", 300 to "+5m", 900 to "+15m").forEach { (delta, label) ->
                    OutlinedButton(
                        onClick = { onAdjustSeconds(delta) },
                        modifier = Modifier.height(34.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Play/Pause/Reset Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Stage
                IconButton(
                    onClick = onPrevious,
                    enabled = canGoPrevious,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (canGoPrevious) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                        .testTag("timer_prev_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Previous Stage",
                        tint = if (canGoPrevious) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }

                // Reset Button
                IconButton(
                    onClick = onReset,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .testTag("timer_reset_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Stage",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Big Primary Play / Pause Button
                Button(
                    onClick = {
                        if (isRunning) onPause() else onStart()
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = stageColor),
                    modifier = Modifier
                        .size(68.dp)
                        .testTag("timer_start_pause_btn"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start Countdown",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Next Stage
                IconButton(
                    onClick = onSkipNext,
                    enabled = canGoNext,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (canGoNext) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                        .testTag("timer_next_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Next Stage",
                        tint = if (canGoNext) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

/**
 * Individual Timeline Item
 */
@Composable
private fun StageTimelineItem(
    stage: BreadStageTimer,
    stepNumber: Int,
    isCurrent: Boolean,
    isRoutineRunning: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val stageColor = when (stage.category) {
        TimerStageCategory.KNEADING -> BreadCrustGolden
        TimerStageCategory.BULK_PROOF, TimerStageCategory.FINAL_PROOF -> ProofingBlue
        TimerStageCategory.BAKING -> BakeFireOrange
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("timeline_item_$stepNumber"),
        shape = RoundedCornerShape(12.dp),
        color = if (isCurrent) stageColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
            if (isCurrent) 1.5.dp else 1.dp,
            if (isCurrent) stageColor else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Step Number or Check
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isCurrent) stageColor else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (stage.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        text = "$stepNumber",
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Stage Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stage.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${stage.category.label} • ${stage.formattedTargetDuration}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Remaining Time badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isCurrent && isRoutineRunning) stageColor else MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = stage.formattedRemaining,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isCurrent && isRoutineRunning) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            // Edit button
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit duration",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Delete button for custom stages
            if (onDelete != null) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete stage",
                        tint = AlertBerryRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Quick Presets Card for launching instant timers
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickPresetsCard(
    onQuickTimer: (String, TimerStageCategory, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick One-Touch Timers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Add preset stage to active schedule with one tap:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple("15m Knead", TimerStageCategory.KNEADING, 15),
                    Triple("30m Autolyse", TimerStageCategory.AUTOLYSE, 30),
                    Triple("45m Proof", TimerStageCategory.FINAL_PROOF, 45),
                    Triple("60m Bulk Rise", TimerStageCategory.BULK_PROOF, 60),
                    Triple("90m Sourdough Rise", TimerStageCategory.BULK_PROOF, 90),
                    Triple("35m Dutch Oven Bake", TimerStageCategory.BAKING, 35),
                    Triple("50m Artisan Bake", TimerStageCategory.BAKING, 50),
                    Triple("60m Wire Rack Cool", TimerStageCategory.COOLING, 60)
                ).forEach { (label, category, minutes) ->
                    OutlinedButton(
                        onClick = { onQuickTimer(label, category, minutes) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(36.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text(text = "+ $label", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/**
 * Dialog to Edit Stage Countdown Duration
 */
@Composable
private fun EditStageDurationDialog(
    stage: BreadStageTimer,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var hoursStr by remember { mutableStateOf("${stage.targetDurationSeconds / 3600}") }
    var minutesStr by remember { mutableStateOf("${(stage.targetDurationSeconds % 3600) / 60}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Set Countdown: ${stage.name}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    text = "Configure custom countdown duration for this bread stage:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = hoursStr,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d{0,2}$"""))) hoursStr = it },
                        label = { Text("Hours") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = minutesStr,
                        onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d{0,3}$"""))) minutesStr = it },
                        label = { Text("Minutes") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick preset buttons for this stage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(15, 30, 45, 60, 90).forEach { presetMins ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    hoursStr = "${presetMins / 60}"
                                    minutesStr = "${presetMins % 60}"
                                }
                        ) {
                            Text(
                                text = "${presetMins}m",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val h = hoursStr.toIntOrNull() ?: 0
                    val m = minutesStr.toIntOrNull() ?: 0
                    val totalSecs = (h * 3600) + (m * 60)
                    if (totalSecs > 0) {
                        onSave(totalSecs)
                    }
                }
            ) {
                Text("Save Countdown")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Dialog to Add a Custom Bread Stage
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddCustomStageDialog(
    onDismiss: () -> Unit,
    onAdd: (String, TimerStageCategory, Int, String) -> Unit
) {
    var stageName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TimerStageCategory.KNEADING) }
    var durationMinutesStr by remember { mutableStateOf("30") }
    var bakerTips by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Bread Stage", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = stageName,
                    onValueChange = { stageName = it },
                    label = { Text("Stage Name (e.g. Fridge Cold Retardation)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Stage Category:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        TimerStageCategory.KNEADING,
                        TimerStageCategory.AUTOLYSE,
                        TimerStageCategory.BULK_PROOF,
                        TimerStageCategory.FINAL_PROOF,
                        TimerStageCategory.BAKING,
                        TimerStageCategory.COOLING
                    ).forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label.substringBefore(" ("), fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = durationMinutesStr,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("""^\d{0,4}$"""))) durationMinutesStr = it },
                    label = { Text("Duration in Minutes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bakerTips,
                    onValueChange = { bakerTips = it },
                    label = { Text("Baker's Guidance / Tips (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val minutes = durationMinutesStr.toIntOrNull() ?: 30
                    val finalName = if (stageName.isBlank()) selectedCategory.label else stageName
                    onAdd(finalName, selectedCategory, minutes, bakerTips)
                }
            ) {
                Text("Add Stage")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
