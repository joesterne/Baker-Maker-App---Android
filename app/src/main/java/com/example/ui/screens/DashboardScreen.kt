package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.AutoFixHigh
import com.example.data.local.BreadMachineCycleTrackerEntity
import com.example.data.model.BreadMachineCycle
import com.example.data.model.BreadRecipe
import com.example.data.model.MachineTelemetry
import com.example.ui.components.BreadMachineCycleTrackerCard
import com.example.ui.components.HydrationCalculatorSheet
import com.example.ui.components.IngredientAlertBanner
import com.example.ui.components.LoafTroubleshootingSection
import com.example.ui.components.MachineStatusCard
import com.example.ui.components.SourdoughCountdownTimerCard
import com.example.ui.components.StageProgressStepper
import com.example.ui.components.TemperatureAdjustmentPanel
import com.example.ui.components.TroubleshootingPromptSheet
import com.example.ui.theme.BakeFireOrange
import com.example.ui.theme.BreadCrustGolden
import com.example.ui.theme.BreadWarmWheat

@Composable
fun DashboardScreen(
    telemetry: MachineTelemetry,
    recipes: List<BreadRecipe>,
    onSelectRecipe: (BreadRecipe) -> Unit,
    onNavigateToRecipes: () -> Unit,
    onToggleConnection: () -> Unit,
    onToggleLid: () -> Unit,
    onTogglePanLock: () -> Unit,
    onToggleLamp: () -> Unit,
    onSkipStage: () -> Unit,
    onJumpToStage: (Int) -> Unit,
    onTestNotification: () -> Unit,
    onDismissAlert: () -> Unit,
    onPaddleRemoved: () -> Unit,
    onAdjustProofTemp: (Int) -> Unit,
    onAdjustBakeTemp: (Int) -> Unit,
    onToggleCrustBooster: () -> Unit,
    onSelectCrust: (com.example.data.model.CrustShade) -> Unit,
    onSelectLoafSize: (com.example.data.model.LoafSize) -> Unit,
    onStartCycle: () -> Unit,
    onPauseCycle: () -> Unit,
    onResumeCycle: () -> Unit,
    onStopCycle: () -> Unit,
    onSetSimulationSpeed: (Int) -> Unit,
    onExtendStage: (Int) -> Unit = {},
    onDismissStepAlert: () -> Unit = {},
    onProceedNextStep: () -> Unit = {},
    onTestStepAlert: () -> Unit = {},
    onNavigateToTimers: () -> Unit = {},
    cycleTracker: BreadMachineCycleTrackerEntity? = null,
    onStartTracker: (() -> Unit)? = null,
    onPauseTracker: (() -> Unit)? = null,
    onResumeTracker: (() -> Unit)? = null,
    onResetTracker: (() -> Unit)? = null,
    onSkipTrackedStage: (() -> Unit)? = null,
    onJumpToTrackedStage: ((Int) -> Unit)? = null,
    onSelectTrackedCycle: ((BreadMachineCycle) -> Unit)? = null,
    onSetTrackedSpeed: ((Int) -> Unit)? = null,
    onAcknowledgeAddInAlarm: (() -> Unit)? = null,
    onTestAddInAlarm: (() -> Unit)? = null,
    onUpdateAddInItems: ((List<String>) -> Unit)? = null,
    onToggleChimeSound: ((Boolean) -> Unit)? = null,
    onToggleVibrate: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showHydrationSheet by remember { mutableStateOf(false) }
    var showTroubleshootingSheet by remember { mutableStateOf(false) }
    var showRecipeDropdown by remember { mutableStateOf(false) }
    var showSpeedDropdown by remember { mutableStateOf(false) }

    val activeRecipe = telemetry.activeRecipe

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Program Bar & Speed Simulator Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recipe Selector dropdown
            Box {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showRecipeDropdown = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Recipes",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "CURRENT RECIPE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (activeRecipe?.isFavorite == true) {
                                    Text("⭐ ", fontSize = 12.sp)
                                }
                                Text(
                                    text = activeRecipe?.title ?: "Select Recipe",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Open",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                DropdownMenu(
                    expanded = showRecipeDropdown,
                    onDismissRequest = { showRecipeDropdown = false }
                ) {
                    recipes.take(8).forEach { recipe ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (recipe.isFavorite) {
                                            Text("⭐ ", fontSize = 11.sp)
                                        }
                                        Text(recipe.title, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        "${recipe.category.label}${if (recipe.bakeCount > 0) " • Baked ${recipe.bakeCount}x" else ""} • ${recipe.hydrationPercent}% hydration",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            onClick = {
                                onSelectRecipe(recipe)
                                showRecipeDropdown = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text("Browse All ${recipes.size} Recipes...", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        },
                        onClick = {
                            showRecipeDropdown = false
                            onNavigateToRecipes()
                        }
                    )
                }
            }

            // Quick Tool Shortcuts: Speed & Hydration Calculator
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Simulation Speed Toggle (allows rapid cycle demonstration)
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showSpeedDropdown = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Speed Multiplier",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${telemetry.simulationSpeed}x",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showSpeedDropdown,
                        onDismissRequest = { showSpeedDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("1x Real Time") },
                            onClick = {
                                onSetSimulationSpeed(1)
                                showSpeedDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("10x Demo Speed") },
                            onClick = {
                                onSetSimulationSpeed(10)
                                showSpeedDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("30x Fast Simulation") },
                            onClick = {
                                onSetSimulationSpeed(30)
                                showSpeedDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("60x Ultra Demo") },
                            onClick = {
                                onSetSimulationSpeed(60)
                                showSpeedDropdown = false
                            }
                        )
                    }
                }

                // Baker's Hydration Calculator Shortcut
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showHydrationSheet = true }
                        .testTag("hydration_calc_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Hydration Calculator",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Hydration",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Stage Timers Shortcut
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToTimers() }
                        .testTag("stage_timers_shortcut_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Stage Timers",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Timers",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                // Loaf Doctor & Troubleshooting Shortcut
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BakeFireOrange.copy(alpha = 0.15f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showTroubleshootingSheet = true }
                        .testTag("troubleshooting_shortcut_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "Loaf Doctor & Troubleshooting",
                            tint = BakeFireOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Doctor",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = BakeFireOrange
                        )
                    }
                }
            }
        }

        // Push Ingredient Alert Banner (Appears when dispenser chime or addition is triggered)
        IngredientAlertBanner(
            alert = telemetry.activeAlert,
            onDismiss = onDismissAlert,
            onPaddleRemoved = onPaddleRemoved
        )

        // Machine Telemetry Card
        MachineStatusCard(
            telemetry = telemetry,
            onToggleConnection = onToggleConnection,
            onToggleLid = onToggleLid,
            onTogglePanLock = onTogglePanLock,
            onToggleLamp = onToggleLamp
        )

        // Dedicated Room-Backed Multi-Stage Bread Machine Cycle Tracker with Add-Ins Alarms
        if (cycleTracker != null) {
            BreadMachineCycleTrackerCard(
                tracker = cycleTracker,
                onStart = { onStartTracker?.invoke() },
                onPause = { onPauseTracker?.invoke() },
                onResume = { onResumeTracker?.invoke() },
                onReset = { onResetTracker?.invoke() },
                onSkipStage = { onSkipTrackedStage?.invoke() },
                onJumpToStage = { onJumpToTrackedStage?.invoke(it) },
                onSelectCycle = { onSelectTrackedCycle?.invoke(it) },
                onSetSpeed = { onSetTrackedSpeed?.invoke(it) },
                onAcknowledgeAlarm = { onAcknowledgeAddInAlarm?.invoke() },
                onTestAddInAlarm = { onTestAddInAlarm?.invoke() },
                onUpdateAddInItems = { onUpdateAddInItems?.invoke(it) },
                onToggleChimeSound = { onToggleChimeSound?.invoke(it) },
                onToggleVibrate = { onToggleVibrate?.invoke(it) }
            )
        }

        // Sourdough Proofing & Baking Precision Countdown Timer & Step Completion Alert
        SourdoughCountdownTimerCard(
            telemetry = telemetry,
            onExtendStage = onExtendStage,
            onSkipStage = onSkipStage,
            onDismissStepAlert = onDismissStepAlert,
            onProceedNextStep = onProceedNextStep,
            onTestStepAlert = onTestStepAlert
        )

        // Step-by-Step Progress Timer
        StageProgressStepper(
            telemetry = telemetry,
            onSkipStage = onSkipStage,
            onJumpToStage = onJumpToStage,
            onTestNotification = onTestNotification
        )

        // Remote Temperature & Crust Tuning Controls
        TemperatureAdjustmentPanel(
            telemetry = telemetry,
            onAdjustProofTemp = onAdjustProofTemp,
            onAdjustBakeTemp = onAdjustBakeTemp,
            onToggleCrustBooster = onToggleCrustBooster,
            onSelectCrust = onSelectCrust,
            onSelectLoafSize = onSelectLoafSize,
            onStartCycle = onStartCycle,
            onPauseCycle = onPauseCycle,
            onResumeCycle = onResumeCycle,
            onStopCycle = onStopCycle
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showHydrationSheet) {
        HydrationCalculatorSheet(onDismiss = { showHydrationSheet = false })
    }

    if (showTroubleshootingSheet) {
        TroubleshootingPromptSheet(
            recipe = activeRecipe,
            currentModel = telemetry.model,
            onDismiss = { showTroubleshootingSheet = false },
            onApplyCrustFix = { onSelectCrust(it) }
        )
    }
}
