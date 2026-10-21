package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BreadRecipe
import com.example.ui.screens.BreadMachineCyclesTimerScreen
import com.example.ui.screens.BreadStageTimerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MachineSettingsScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.screens.RecipeEditorScreen
import com.example.ui.screens.RecipeLibraryScreen
import com.example.ui.screens.SourdoughRecipesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BreadMakerViewModel

enum class AppTab(val label: String) {
    DASHBOARD("Baker"),
    SOURDOUGH("Sourdough"),
    RECIPES("Library"),
    TIMERS("Timers"),
    SETTINGS("Settings")
}

sealed class ScreenState {
    data object Tabs : ScreenState()
    data class RecipeDetail(val recipe: BreadRecipe) : ScreenState()
    data object RecipeEditor : ScreenState()
}

class MainActivity : ComponentActivity() {

    private val viewModel: BreadMakerViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val snackbarHostState = remember { SnackbarHostState() }

                // Notification permission launcher for Android 13+
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        viewModel.showFeedback("Notification alerts enabled for baking stages")
                    }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // Show feedback snackbars
                val feedbackMessage by viewModel.userFeedbackMessage.collectAsStateWithLifecycle()
                LaunchedEffect(feedbackMessage) {
                    feedbackMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearFeedback()
                    }
                }

                var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }
                var currentScreen by remember { mutableStateOf<ScreenState>(ScreenState.Tabs) }

                when (val screen = currentScreen) {
                    is ScreenState.RecipeDetail -> {
                        BackHandler {
                            currentScreen = ScreenState.Tabs
                        }
                        val recipes by viewModel.recipes.collectAsStateWithLifecycle()
                        val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
                        val currentRecipe = recipes.find { it.id == screen.recipe.id } ?: screen.recipe
                        RecipeDetailScreen(
                            recipe = currentRecipe,
                            onBack = { currentScreen = ScreenState.Tabs },
                            onLoadAndBake = {
                                viewModel.loadRecipe(currentRecipe)
                                currentScreen = ScreenState.Tabs
                                currentTab = AppTab.DASHBOARD
                            },
                            onSavePhoto = { newPhotoUri ->
                                viewModel.updateRecipePhoto(currentRecipe.id, newPhotoUri)
                            },
                            onToggleFavorite = { viewModel.toggleFavoriteRecipe(it) },
                            currentModel = telemetry.model
                        )
                    }
                    is ScreenState.RecipeEditor -> {
                        BackHandler {
                            currentScreen = ScreenState.Tabs
                        }
                        RecipeEditorScreen(
                            onBack = { currentScreen = ScreenState.Tabs },
                            onSaveRecipe = { newRecipe ->
                                viewModel.saveCustomRecipe(newRecipe)
                                currentScreen = ScreenState.Tabs
                                currentTab = AppTab.SOURDOUGH
                            }
                        )
                    }
                    is ScreenState.Tabs -> {
                        Scaffold(
                            contentWindowInsets = WindowInsets.safeDrawing,
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = when (currentTab) {
                                                AppTab.DASHBOARD -> "CrustCraft Baker"
                                                AppTab.SOURDOUGH -> "Sourdough from Room DB"
                                                AppTab.RECIPES -> "Artisanal Recipe Library"
                                                AppTab.TIMERS -> "Stage Countdown Timers"
                                                AppTab.SETTINGS -> "Machine & Preferences"
                                            },
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.background,
                                        titleContentColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            },
                            bottomBar = {
                                NavigationBar(
                                    modifier = Modifier.testTag("bottom_nav_bar")
                                ) {
                                    NavigationBarItem(
                                        selected = currentTab == AppTab.DASHBOARD,
                                        onClick = { currentTab = AppTab.DASHBOARD },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == AppTab.DASHBOARD) Icons.Filled.Tune else Icons.Outlined.Tune,
                                                contentDescription = "Baker Control",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = { Text("Baker") }
                                    )
                                    NavigationBarItem(
                                        selected = currentTab == AppTab.SOURDOUGH,
                                        onClick = { currentTab = AppTab.SOURDOUGH },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == AppTab.SOURDOUGH) Icons.Filled.Grain else Icons.Outlined.Grain,
                                                contentDescription = "Sourdough Recipes",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = { Text("Sourdough") }
                                    )
                                    NavigationBarItem(
                                        selected = currentTab == AppTab.RECIPES,
                                        onClick = { currentTab = AppTab.RECIPES },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == AppTab.RECIPES) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                                contentDescription = "Recipes",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = { Text("Library") }
                                    )
                                    NavigationBarItem(
                                        selected = currentTab == AppTab.TIMERS,
                                        onClick = { currentTab = AppTab.TIMERS },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == AppTab.TIMERS) Icons.Filled.Timer else Icons.Outlined.Timer,
                                                contentDescription = "Bread Stage Timers",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = { Text("Timers") }
                                    )
                                    NavigationBarItem(
                                        selected = currentTab == AppTab.SETTINGS,
                                        onClick = { currentTab = AppTab.SETTINGS },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == AppTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                                contentDescription = "Machine Settings",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = { Text("Settings") }
                                    )
                                }
                            },
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            modifier = Modifier.fillMaxSize()
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                when (currentTab) {
                                    AppTab.DASHBOARD -> {
                                        DashboardTabContent(
                                            viewModel = viewModel,
                                            onNavigateToRecipes = { currentTab = AppTab.SOURDOUGH },
                                            onNavigateToTimers = { currentTab = AppTab.TIMERS }
                                        )
                                    }
                                    AppTab.SOURDOUGH -> {
                                        SourdoughTabContent(
                                            viewModel = viewModel,
                                            onSelectRecipe = { recipe ->
                                                currentScreen = ScreenState.RecipeDetail(recipe)
                                            },
                                            onLoadIntoBreadMaker = { recipe ->
                                                viewModel.loadRecipe(recipe)
                                                currentTab = AppTab.DASHBOARD
                                            },
                                            onCreateCustomSourdough = {
                                                currentScreen = ScreenState.RecipeEditor
                                            },
                                            onViewLiveCountdown = {
                                                currentTab = AppTab.DASHBOARD
                                            }
                                        )
                                    }
                                    AppTab.RECIPES -> {
                                        RecipeLibraryTabContent(
                                            viewModel = viewModel,
                                            onRecipeClick = { recipe ->
                                                currentScreen = ScreenState.RecipeDetail(recipe)
                                            },
                                            onLoadRecipeIntoMachine = { recipe ->
                                                viewModel.loadRecipe(recipe)
                                                currentTab = AppTab.DASHBOARD
                                            },
                                            onCreateCustomRecipe = {
                                                currentScreen = ScreenState.RecipeEditor
                                            }
                                        )
                                    }
                                    AppTab.TIMERS -> {
                                        var timerSubTab by remember { mutableStateOf(0) }
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            PrimaryTabRow(
                                                selectedTabIndex = timerSubTab,
                                                containerColor = MaterialTheme.colorScheme.surface,
                                                contentColor = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("timers_tab_row")
                                            ) {
                                                Tab(
                                                    selected = timerSubTab == 0,
                                                    onClick = { timerSubTab = 0 },
                                                    text = {
                                                        Text(
                                                            "Machine Cycles",
                                                            fontWeight = if (timerSubTab == 0) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                    },
                                                    icon = {
                                                        Icon(
                                                            imageVector = Icons.Default.HourglassBottom,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    },
                                                    modifier = Modifier.testTag("tab_machine_cycles")
                                                )
                                                Tab(
                                                    selected = timerSubTab == 1,
                                                    onClick = { timerSubTab = 1 },
                                                    text = {
                                                        Text(
                                                            "Stage Workflows",
                                                            fontWeight = if (timerSubTab == 1) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                    },
                                                    icon = {
                                                        Icon(
                                                            imageVector = Icons.Default.Schedule,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    },
                                                    modifier = Modifier.testTag("tab_stage_workflows")
                                                )
                                            }

                                            if (timerSubTab == 0) {
                                                val activeTracker by viewModel.activeCycleTracker.collectAsStateWithLifecycle()
                                                BreadMachineCyclesTimerScreen(
                                                    tracker = activeTracker,
                                                    onStartTracker = { viewModel.startTrackedCycle() },
                                                    onPauseTracker = { viewModel.pauseTrackedCycle() },
                                                    onResumeTracker = { viewModel.resumeTrackedCycle() },
                                                    onResetTracker = { viewModel.resetTrackedCycle() },
                                                    onSkipTrackedStage = { viewModel.skipToNextTrackedStage() },
                                                    onJumpToTrackedStage = { viewModel.jumpToTrackedStage(it) },
                                                    onSelectTrackedCycle = { viewModel.selectCycleForTracker(it) },
                                                    onSetTrackedSpeed = { viewModel.setTrackedCycleSimulationSpeed(it) },
                                                    onAcknowledgeAddInAlarm = { viewModel.acknowledgeAddInAlarm() },
                                                    onTestAddInAlarm = { viewModel.triggerTestAddInAlarm() },
                                                    onUpdateAddInItems = { viewModel.updateAddInItems(it) },
                                                    onToggleChimeSound = { viewModel.toggleAddInChimeSound(it) },
                                                    onToggleVibrate = { viewModel.toggleAddInVibrate(it) },
                                                    onLoadCycleIntoBaker = { cycle ->
                                                        viewModel.setZojirushiMenuCourse(cycle.course)
                                                        currentTab = AppTab.DASHBOARD
                                                    }
                                                )
                                            } else {
                                                val stageTimersState by viewModel.stageTimersState.collectAsStateWithLifecycle()
                                                BreadStageTimerScreen(
                                                    state = stageTimersState,
                                                    onSelectRoutine = { viewModel.selectTimerRoutine(it) },
                                                    onSelectStage = { viewModel.selectStage(it) },
                                                    onStartCountdown = { viewModel.startStageCountdown() },
                                                    onPauseCountdown = { viewModel.pauseStageCountdown() },
                                                    onResumeCountdown = { viewModel.resumeStageCountdown() },
                                                    onResetCountdown = { viewModel.resetCurrentStageCountdown() },
                                                    onAdjustCountdown = { viewModel.adjustCurrentStageCountdown(it) },
                                                    onSetStageDuration = { idx, secs -> viewModel.setStageDuration(idx, secs) },
                                                    onSkipNextStage = { viewModel.skipToNextStageTimer() },
                                                    onPreviousStage = { viewModel.previousStageTimer() },
                                                    onAddCustomStage = { name, cat, secs, tips -> viewModel.addCustomStageTimer(name, cat, secs, tips) },
                                                    onDeleteStage = { viewModel.deleteStageTimer(it) },
                                                    onDismissAlert = { viewModel.dismissStageTimerAlert() }
                                                )
                                            }
                                        }
                                    }
                                    AppTab.SETTINGS -> {
                                        MachineSettingsTabContent(viewModel = viewModel)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardTabContent(
    viewModel: BreadMakerViewModel,
    onNavigateToRecipes: () -> Unit,
    onNavigateToTimers: () -> Unit
) {
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val recipes by viewModel.recipes.collectAsStateWithLifecycle()
    val activeTracker by viewModel.activeCycleTracker.collectAsStateWithLifecycle()

    DashboardScreen(
        telemetry = telemetry,
        recipes = recipes,
        onSelectRecipe = { viewModel.loadRecipe(it) },
        onNavigateToRecipes = onNavigateToRecipes,
        onToggleConnection = { viewModel.toggleConnection() },
        onToggleLid = { viewModel.toggleLid() },
        onTogglePanLock = { viewModel.togglePanLock() },
        onToggleLamp = { viewModel.toggleInteriorLamp() },
        onSkipStage = { viewModel.skipToNextStage() },
        onJumpToStage = { viewModel.jumpToStage(it) },
        onTestNotification = { viewModel.testIngredientNotification() },
        onDismissAlert = { viewModel.dismissCurrentAlert() },
        onPaddleRemoved = { viewModel.markPaddleRemoved() },
        onAdjustProofTemp = { viewModel.adjustProofTemp(it) },
        onAdjustBakeTemp = { viewModel.adjustBakeTemp(it) },
        onToggleCrustBooster = { viewModel.toggleCrustBooster() },
        onSelectCrust = { viewModel.setCrustShade(it) },
        onSelectLoafSize = { viewModel.setLoafSize(it) },
        onStartCycle = { viewModel.startBakingCycle() },
        onPauseCycle = { viewModel.pauseBakingCycle() },
        onResumeCycle = { viewModel.resumeBakingCycle() },
        onStopCycle = { viewModel.stopBakingCycle() },
        onSetSimulationSpeed = { viewModel.setSimulationSpeed(it) },
        onExtendStage = { viewModel.extendCurrentStage(it) },
        onDismissStepAlert = { viewModel.dismissStepCompletionAlert() },
        onProceedNextStep = { viewModel.proceedToNextStep() },
        onTestStepAlert = { viewModel.testProofingCountdownAlert() },
        onNavigateToTimers = onNavigateToTimers,
        cycleTracker = activeTracker,
        onStartTracker = { viewModel.startTrackedCycle() },
        onPauseTracker = { viewModel.pauseTrackedCycle() },
        onResumeTracker = { viewModel.resumeTrackedCycle() },
        onResetTracker = { viewModel.resetTrackedCycle() },
        onSkipTrackedStage = { viewModel.skipToNextTrackedStage() },
        onJumpToTrackedStage = { viewModel.jumpToTrackedStage(it) },
        onSelectTrackedCycle = { viewModel.selectCycleForTracker(it) },
        onSetTrackedSpeed = { viewModel.setTrackedCycleSimulationSpeed(it) },
        onAcknowledgeAddInAlarm = { viewModel.acknowledgeAddInAlarm() },
        onTestAddInAlarm = { viewModel.triggerTestAddInAlarm() },
        onUpdateAddInItems = { viewModel.updateAddInItems(it) },
        onToggleChimeSound = { viewModel.toggleAddInChimeSound(it) },
        onToggleVibrate = { viewModel.toggleAddInVibrate(it) }
    )
}

@Composable
private fun SourdoughTabContent(
    viewModel: BreadMakerViewModel,
    onSelectRecipe: (BreadRecipe) -> Unit,
    onLoadIntoBreadMaker: (BreadRecipe) -> Unit,
    onCreateCustomSourdough: () -> Unit,
    onViewLiveCountdown: () -> Unit
) {
    val sourdoughRecipes by viewModel.sourdoughRecipes.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()

    SourdoughRecipesScreen(
        sourdoughRecipes = sourdoughRecipes,
        onSelectRecipe = onSelectRecipe,
        onLoadIntoBreadMaker = onLoadIntoBreadMaker,
        onCreateCustomSourdough = onCreateCustomSourdough,
        telemetry = telemetry,
        onViewLiveCountdown = onViewLiveCountdown
    )
}

@Composable
private fun RecipeLibraryTabContent(
    viewModel: BreadMakerViewModel,
    onRecipeClick: (BreadRecipe) -> Unit,
    onLoadRecipeIntoMachine: (BreadRecipe) -> Unit,
    onCreateCustomRecipe: () -> Unit
) {
    val filteredRecipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categoryCounts by viewModel.categoryCounts.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()

    RecipeLibraryScreen(
        recipes = filteredRecipes,
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        categoryCounts = categoryCounts,
        onSearchChange = { viewModel.onSearchQueryChange(it) },
        onCategoryChange = { viewModel.onCategorySelect(it) },
        onRecipeClick = onRecipeClick,
        onLoadRecipeIntoMachine = onLoadRecipeIntoMachine,
        onCreateCustomRecipe = onCreateCustomRecipe,
        onDeleteCustomRecipe = { viewModel.deleteCustomRecipe(it) },
        onToggleFavorite = { viewModel.toggleFavoriteRecipe(it) },
        currentModel = telemetry.model
    )
}

@Composable
private fun MachineSettingsTabContent(
    viewModel: BreadMakerViewModel
) {
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()

    MachineSettingsScreen(
        telemetry = telemetry,
        onToggleConnection = { viewModel.toggleConnection() },
        onSelectModel = { viewModel.selectBreadMakerModel(it) },
        onSelectZojirushiCourse = { viewModel.setZojirushiMenuCourse(it) },
        onSelectMemorySlot = { viewModel.setHomemadeMemorySlot(it) },
        onTogglePaddleReminder = { viewModel.togglePaddleRemovalReminder(it) },
        onSetSimulationSpeed = { viewModel.setSimulationSpeed(it) },
        onTestNotification = { viewModel.testIngredientNotification() },
        onUpdateConverterSettings = { viewModel.updateConverterSettings(it) }
    )
}
