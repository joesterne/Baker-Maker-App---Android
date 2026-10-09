package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PreloadedRecipes
import com.example.data.model.BakingStage
import com.example.data.model.BakingStageType
import com.example.data.model.BreadMakerModel
import com.example.data.model.BreadRecipe
import com.example.data.model.BreadStageTimer
import com.example.data.model.CrustShade
import com.example.data.model.FlourCategory
import com.example.data.model.IngredientAlert
import com.example.data.model.LoafSize
import com.example.data.model.MachineState
import com.example.data.model.MachineTelemetry
import com.example.data.model.PreloadedTimerRoutines
import com.example.data.model.StageTimersState
import com.example.data.model.StepCompletionAlert
import com.example.data.model.TimerRoutine
import com.example.data.model.TimerStageCategory
import com.example.data.model.ZojirushiMenuCourse
import com.example.data.repository.RecipeRepository
import com.example.notification.BreadNotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BreadMakerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository
    private val notificationHelper: BreadNotificationHelper = BreadNotificationHelper(application)

    private val _telemetry = MutableStateFlow(MachineTelemetry())
    val telemetry: StateFlow<MachineTelemetry> = _telemetry.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _customSearchQuery = MutableStateFlow("")
    val customSearchQuery: StateFlow<String> = _customSearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FlourCategory.ALL)
    val selectedCategory: StateFlow<FlourCategory> = _selectedCategory.asStateFlow()

    private val _selectedRecipe = MutableStateFlow<BreadRecipe?>(null)
    val selectedRecipe: StateFlow<BreadRecipe?> = _selectedRecipe.asStateFlow()

    // Alert history / snackbar message
    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Bread Stage Countdown Timer State
    private val _stageTimersState = MutableStateFlow(StageTimersState())
    val stageTimersState: StateFlow<StageTimersState> = _stageTimersState.asStateFlow()

    private var stageCountdownJob: Job? = null
    private var tickerJob: Job? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = RecipeRepository(db.recipeDao(), db.customRecipeDao())

        viewModelScope.launch {
            repository.ensureSeeded()
        }

        // Default to San Francisco Sourdough as active preview recipe
        val defaultRecipe = PreloadedRecipes.SOURDOUGH_RECIPES.first()
        loadRecipe(defaultRecipe)
    }

    val sourdoughRecipes: StateFlow<List<BreadRecipe>> = repository.sourdoughRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PreloadedRecipes.SOURDOUGH_RECIPES
        )

    val recipes: StateFlow<List<BreadRecipe>> = repository.allRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PreloadedRecipes.RECIPES
        )

    // Reactive flow of custom bread recipes stored in Room
    val customRecipes: StateFlow<List<BreadRecipe>> = repository.customRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Direct Room query search results for custom bread recipes
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchedCustomRecipes: StateFlow<List<BreadRecipe>> = _customSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.customRecipes
            } else {
                repository.searchCustomRecipes(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredRecipes: StateFlow<List<BreadRecipe>> = combine(
        recipes,
        _searchQuery,
        _selectedCategory
    ) { all, query, category ->
        all.filter { recipe ->
            val matchesCategory = (category == FlourCategory.ALL) ||
                    (category == FlourCategory.CUSTOM && recipe.isCustom) ||
                    (recipe.category == category)
            val matchesQuery = query.isBlank() ||
                    recipe.title.contains(query, ignoreCase = true) ||
                    recipe.primaryFlour.contains(query, ignoreCase = true) ||
                    recipe.specialtyFlours.any { it.contains(query, ignoreCase = true) } ||
                    recipe.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PreloadedRecipes.RECIPES
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCustomSearchQueryChange(query: String) {
        _customSearchQuery.value = query
    }

    fun onCategorySelect(category: FlourCategory) {
        _selectedCategory.value = category
    }

    fun selectRecipeForDetail(recipe: BreadRecipe?) {
        _selectedRecipe.value = recipe
    }

    fun clearFeedback() {
        _userFeedbackMessage.value = null
    }

    fun showFeedback(msg: String) {
        _userFeedbackMessage.value = msg
    }

    fun selectBreadMakerModel(model: BreadMakerModel) {
        _telemetry.update { current ->
            current.copy(
                model = model,
                machineName = model.modelName,
                connectionType = if (model == BreadMakerModel.ZOJIRUSHI_BB_PDC20) "Bluetooth 5.2 LE (Zojirushi Connect)" else "Bluetooth 5.2 LE",
                selectedLoafSize = if (model == BreadMakerModel.ZOJIRUSHI_BB_PDC20) LoafSize.TWO_LB else current.selectedLoafSize
            )
        }
        showFeedback("Bread maker model set to ${model.shortName}")
    }

    fun setZojirushiMenuCourse(course: ZojirushiMenuCourse) {
        _telemetry.update { it.copy(zojirushiMenuCourse = course) }
        showFeedback("Zojirushi Course #${course.courseNumber}: ${course.courseName}")
    }

    fun setHomemadeMemorySlot(slot: Int) {
        val clamped = slot.coerceIn(1, 3)
        _telemetry.update { it.copy(homemadeMemorySlot = clamped) }
        showFeedback("Zojirushi Memory Slot #$clamped selected")
    }

    fun markTwinPaddlesRemoved() {
        _telemetry.update { it.copy(paddleRemoved = true, twinPaddlesRemoved = true, activeAlert = null) }
        showFeedback("Twin kneading blades extracted from Zojirushi pan. Ready for final proof.")
    }

    // Machine Hardware Telemetry Actions
    fun toggleConnection() {
        _telemetry.update { current ->
            val newConnected = !current.isConnected
            current.copy(
                isConnected = newConnected,
                state = if (!newConnected && current.state == MachineState.RUNNING) MachineState.PAUSED else current.state
            )
        }
        showFeedback(if (_telemetry.value.isConnected) "Connected to ${_telemetry.value.machineName}" else "Disconnected from bread machine")
    }

    fun toggleLid() {
        _telemetry.update { it.copy(lidOpen = !it.lidOpen) }
        val isOpen = _telemetry.value.lidOpen
        if (isOpen && _telemetry.value.state == MachineState.RUNNING) {
            showFeedback("⚠️ Warning: Lid opened during baking cycle. Heat loss in chamber!")
        } else {
            showFeedback(if (isOpen) "Inspection lid opened" else "Lid securely closed")
        }
    }

    fun togglePanLock() {
        _telemetry.update { it.copy(panLocked = !it.panLocked) }
        showFeedback(if (_telemetry.value.panLocked) "Bread pan locked into drive motor" else "Bread pan unlocked")
    }

    fun toggleInteriorLamp() {
        _telemetry.update { it.copy(interiorLampOn = !it.interiorLampOn) }
    }

    fun setSimulationSpeed(speed: Int) {
        _telemetry.update { it.copy(simulationSpeed = speed.coerceIn(1, 60)) }
        showFeedback("Simulation speed set to ${speed}x")
    }

    fun setCrustShade(shade: CrustShade) {
        _telemetry.update { current ->
            val updated = current.copy(selectedCrust = shade)
            recalculateTargetTemp(updated)
        }
        showFeedback("Crust set to ${shade.label}: ${shade.description}")
    }

    fun setLoafSize(size: LoafSize) {
        _telemetry.update { it.copy(selectedLoafSize = size) }
        showFeedback("Loaf weight set to ${size.label} (${size.weightGrams}g)")
    }

    fun adjustProofTemp(deltaF: Int) {
        _telemetry.update { current ->
            val newOffset = (current.proofTempOffsetF + deltaF).coerceIn(-15, 15)
            val updated = current.copy(proofTempOffsetF = newOffset)
            recalculateTargetTemp(updated)
        }
    }

    fun adjustBakeTemp(deltaF: Int) {
        _telemetry.update { current ->
            val newOffset = (current.bakeTempOffsetF + deltaF).coerceIn(-40, 40)
            val updated = current.copy(bakeTempOffsetF = newOffset)
            recalculateTargetTemp(updated)
        }
    }

    fun toggleCrustBooster() {
        _telemetry.update { current ->
            val newBoost = !current.crustBoosterActive
            val updated = current.copy(crustBoosterActive = newBoost)
            recalculateTargetTemp(updated)
        }
        val isBoost = _telemetry.value.crustBoosterActive
        showFeedback(if (isBoost) "🔥 Crust Booster ON (+20°F radiant top element)" else "Crust Booster OFF")
    }

    fun togglePaddleRemovalReminder(enabled: Boolean) {
        _telemetry.update { it.copy(paddleRemovalReminderEnabled = enabled) }
    }

    // Measurement & Converter Preferences
    fun updateConverterSettings(settings: com.example.data.model.ConverterSettings) {
        _telemetry.update { it.copy(converterSettings = settings) }
        showFeedback("Measurement converter preferences updated")
    }

    fun setPreferredUnitSystem(unitSystem: com.example.data.model.UnitSystem) {
        _telemetry.update {
            it.copy(converterSettings = it.converterSettings.copy(preferredUnitSystem = unitSystem))
        }
        showFeedback("Unit preference set to ${unitSystem.label}")
    }

    fun setDefaultConverterLiquid(liquid: com.example.data.model.LiquidDensityItem) {
        _telemetry.update {
            it.copy(converterSettings = it.converterSettings.copy(defaultLiquid = liquid))
        }
        showFeedback("Default liquid converter set to ${liquid.displayName}")
    }

    fun setCupStandard(cupMl: Double) {
        _telemetry.update {
            it.copy(converterSettings = it.converterSettings.copy(cupStandardMl = cupMl))
        }
        showFeedback("Cup standard set to ${cupMl.toInt()} mL")
    }

    fun toggleAutoHydrationDisplay(enabled: Boolean) {
        _telemetry.update {
            it.copy(converterSettings = it.converterSettings.copy(autoDisplayHydrationPercentage = enabled))
        }
    }

    fun setDelayTimer(minutes: Int) {
        _telemetry.update { it.copy(delayStartMinutes = minutes.coerceAtLeast(0)) }
        if (minutes > 0) {
            val hours = minutes / 60
            val mins = minutes % 60
            showFeedback("Delay start timer set: baking begins in ${hours}h ${mins}m")
        } else {
            showFeedback("Delay timer cleared")
        }
    }

    fun loadRecipe(recipe: BreadRecipe) {
        stopBakingCycle()
        val totalSecs = recipe.stages.sumOf { it.durationMinutes * 60 }
        val firstStage = recipe.stages.firstOrNull()
        val firstStageSecs = (firstStage?.durationMinutes ?: 0) * 60

        _telemetry.update { current ->
            val updated = current.copy(
                activeRecipe = recipe,
                selectedCrust = recipe.defaultCrust,
                selectedLoafSize = recipe.defaultLoafSize,
                currentStageIndex = 0,
                stageElapsedSeconds = 0,
                stageDurationSeconds = firstStageSecs,
                totalElapsedSeconds = 0,
                totalDurationSeconds = totalSecs,
                state = MachineState.IDLE,
                activeAlert = null,
                paddleRemoved = false,
                motorRpm = 0,
                heatingElementActive = false
            )
            recalculateTargetTemp(updated)
        }
        showFeedback("Loaded \"${recipe.title}\" program into bread maker")
    }

    fun startBakingCycle() {
        val current = _telemetry.value
        if (!current.isConnected) {
            showFeedback("⚠️ Cannot start: Connect to CrustMaster bread maker first!")
            return
        }
        if (!current.panLocked) {
            showFeedback("⚠️ Lock the bread pan into base before starting motor!")
            return
        }

        _telemetry.update { it.copy(state = MachineState.RUNNING) }
        startTicker()
        showFeedback("Baking cycle started: ${current.activeRecipe?.title ?: "Program"}")
        checkForStageAlert(current.currentStageIndex, isStageStart = true)
    }

    fun pauseBakingCycle() {
        _telemetry.update { it.copy(state = MachineState.PAUSED, motorRpm = 0, heatingElementActive = false) }
        showFeedback("Baking cycle paused. Press Resume to continue.")
    }

    fun resumeBakingCycle() {
        _telemetry.update { it.copy(state = MachineState.RUNNING) }
        startTicker()
        showFeedback("Baking cycle resumed")
    }

    fun stopBakingCycle() {
        tickerJob?.cancel()
        tickerJob = null
        _telemetry.update { current ->
            current.copy(
                state = MachineState.IDLE,
                stageElapsedSeconds = 0,
                totalElapsedSeconds = 0,
                currentStageIndex = 0,
                motorRpm = 0,
                heatingElementActive = false,
                activeAlert = null
            )
        }
        showFeedback("Baking cycle cancelled")
    }

    fun skipToNextStage() {
        val current = _telemetry.value
        val recipe = current.activeRecipe ?: return
        val nextIndex = current.currentStageIndex + 1

        if (nextIndex < recipe.stages.size) {
            transitionToStage(nextIndex)
            showFeedback("Skipped to: ${recipe.stages[nextIndex].title}")
        } else {
            completeCycle()
        }
    }

    fun jumpToStage(stageIndex: Int) {
        val recipe = _telemetry.value.activeRecipe ?: return
        if (stageIndex in recipe.stages.indices) {
            transitionToStage(stageIndex)
            showFeedback("Jumped to: ${recipe.stages[stageIndex].title}")
        }
    }

    fun dismissCurrentAlert() {
        _telemetry.update { it.copy(activeAlert = null) }
    }

    fun dismissStepCompletionAlert() {
        _telemetry.update { it.copy(activeStepCompletionAlert = null) }
    }

    fun proceedToNextStep() {
        _telemetry.update { it.copy(activeStepCompletionAlert = null) }
        skipToNextStage()
    }

    fun extendCurrentStage(minutes: Int) {
        val extraSecs = minutes * 60
        _telemetry.update { current ->
            current.copy(
                stageDurationSeconds = current.stageDurationSeconds + extraSecs,
                totalDurationSeconds = current.totalDurationSeconds + extraSecs,
                activeStepCompletionAlert = null
            )
        }
        showFeedback("Proofing extended by +$minutes minutes")
    }

    fun testProofingCountdownAlert() {
        val recipe = _telemetry.value.activeRecipe ?: PreloadedRecipes.SOURDOUGH_RECIPES.first()
        val proofStage = recipe.stages.find { it.type == BakingStageType.RISE_3 } ?: recipe.stages[4]
        val nextStage = recipe.stages.find { it.type == BakingStageType.BAKE }

        val alert = StepCompletionAlert(
            completedStage = proofStage,
            nextStage = nextStage,
            isProofing = true,
            isBaking = false,
            title = "Final Proof Complete! 🍞",
            message = "Sourdough has crowned to full volume. Poke test: dough gently springs back. Time to score dome with razor and begin baking.",
            actionPrompt = "Score Top & Start Bake"
        )

        notificationHelper.sendStepCompleteNotification(
            completedStageName = proofStage.title,
            nextStageName = nextStage?.title,
            guidance = alert.message,
            isProofingStep = true
        )

        _telemetry.update { it.copy(activeStepCompletionAlert = alert) }
        showFeedback("⏰ Step complete alert triggered: Sourdough Final Proof!")
    }

    fun testIngredientNotification() {
        val recipe = _telemetry.value.activeRecipe ?: PreloadedRecipes.RECIPES.first()
        val stage = recipe.stages.find { it.hasIngredientAlert } ?: recipe.stages.first()
        val title = stage.alertTitle ?: "Ingredient Chime"
        val desc = stage.alertDescription ?: "Dispenser chime: Add seeds & butter now!"

        notificationHelper.sendIngredientAlert(
            title = title,
            message = desc,
            stageName = stage.title,
            itemsToAdd = listOf("50g Toasted Walnuts", "35g Chilled Diced Butter")
        )

        _telemetry.update {
            it.copy(
                activeAlert = IngredientAlert(
                    title = title,
                    message = desc,
                    stageType = stage.type,
                    itemsToAdd = listOf("Toasted Walnuts / Dried Cranberries", "Cold Chilled Butter")
                )
            )
        }
        showFeedback("🔔 Sent push notification & vibration alert!")
    }

    fun markPaddleRemoved() {
        _telemetry.update { it.copy(paddleRemoved = true, activeAlert = null) }
        showFeedback("Kneading blade marked as removed. Dough continues proofing.")
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive && _telemetry.value.state == MachineState.RUNNING) {
                val speed = _telemetry.value.simulationSpeed
                delay(1000L) // 1 real second tick
                advanceTimer(secondsDelta = speed)
            }
        }
    }

    private fun advanceTimer(secondsDelta: Int) {
        _telemetry.update { current ->
            if (current.state != MachineState.RUNNING) return@update current
            val recipe = current.activeRecipe ?: return@update current
            val stage = recipe.stages.getOrNull(current.currentStageIndex) ?: return@update current

            val newStageElapsed = current.stageElapsedSeconds + secondsDelta
            val newTotalElapsed = current.totalElapsedSeconds + secondsDelta

            // Update motor RPM and heating based on stage
            val currentMotorRpm = when (stage.type) {
                BakingStageType.KNEAD_1 -> 45
                BakingStageType.KNEAD_2 -> 115
                BakingStageType.PUNCH_DOWN -> 30
                else -> 0
            }
            val leftBlade = if (current.model.bladeCount == 2) currentMotorRpm else currentMotorRpm
            val rightBlade = if (current.model.bladeCount == 2) currentMotorRpm else 0

            val isHeating = stage.heatingPowerPercent > 0 || stage.type == BakingStageType.BAKE
            val isBaking = stage.type == BakingStageType.BAKE
            val lidHeater = isBaking && (current.model.hasLidHeater || current.crustBoosterActive)
            val baseHeater = isHeating

            // Simulate realistic chamber temperature curve towards target
            val target = current.chamberTargetTempF
            val currentTemp = current.chamberTempF
            val tempDiff = target - currentTemp
            val tempStep = (tempDiff * 0.08f * (secondsDelta / 1f)).coerceIn(-4f * secondsDelta, 4f * secondsDelta)
            val newChamberTemp = (currentTemp + tempStep).coerceIn(70f, 420f)

            if (newStageElapsed >= current.stageDurationSeconds) {
                // Stage complete! Transition will happen
                val nextIndex = current.currentStageIndex + 1
                if (nextIndex < recipe.stages.size) {
                    val nextStage = recipe.stages[nextIndex]
                    val nextStageSecs = nextStage.durationMinutes * 60

                    val isProofing = stage.type in listOf(BakingStageType.RISE_1, BakingStageType.RISE_2, BakingStageType.RISE_3, BakingStageType.REST_AUTOLYSE)
                    val isBaking = stage.type == BakingStageType.BAKE

                    val (stepTitle, stepMsg, actionPrompt) = when (stage.type) {
                        BakingStageType.REST_AUTOLYSE -> Triple(
                            "Autolyse Rest Complete! 🌾",
                            "Flour hydration finished. Next: Gluten development kneading.",
                            "Continue Knead"
                        )
                        BakingStageType.RISE_1 -> Triple(
                            "Bulk Fermentation Complete! ⏳",
                            "Bulk rise finished. Dough has expanded with wild yeast carbon dioxide. Ready for degas & shaping.",
                            "Degas Dough"
                        )
                        BakingStageType.RISE_2 -> Triple(
                            "Second Rise Complete! ⏳",
                            "Intermediate proofing finished. Dough structure has rested.",
                            "Final Proof"
                        )
                        BakingStageType.RISE_3 -> Triple(
                            "Final Proof Complete! 🍞",
                            "Sourdough dome has finished rising. Ready to slash top with razor and mist for blistered crust.",
                            "Score & Bake"
                        )
                        BakingStageType.BAKE -> Triple(
                            "Baking Complete! 🔥",
                            "Artisan crust has fully browned. Unmold within 15 minutes.",
                            "Done"
                        )
                        else -> Triple(
                            "Step Complete: ${stage.title} ⏰",
                            "Completed ${stage.durationMinutes}-min cycle for ${stage.title}. Next: ${nextStage.title}.",
                            "Next Step"
                        )
                    }

                    val stepAlert = StepCompletionAlert(
                        completedStage = stage,
                        nextStage = nextStage,
                        isProofing = isProofing,
                        isBaking = isBaking,
                        title = stepTitle,
                        message = stepMsg,
                        actionPrompt = actionPrompt
                    )

                    notificationHelper.sendStepCompleteNotification(
                        completedStageName = stage.title,
                        nextStageName = nextStage.title,
                        guidance = stepMsg,
                        isProofingStep = isProofing
                    )

                    viewModelScope.launch {
                        checkForStageAlert(nextIndex, isStageStart = true)
                    }
                    val updated = current.copy(
                        currentStageIndex = nextIndex,
                        stageElapsedSeconds = 0,
                        stageDurationSeconds = nextStageSecs,
                        totalElapsedSeconds = newTotalElapsed,
                        chamberTempF = newChamberTemp,
                        motorRpm = currentMotorRpm,
                        leftBladeRpm = leftBlade,
                        rightBladeRpm = rightBlade,
                        heatingElementActive = isHeating,
                        baseHeaterActive = baseHeater,
                        lidHeaterActive = lidHeater,
                        activeStepCompletionAlert = stepAlert
                    )
                    recalculateTargetTemp(updated)
                } else {
                    // All stages complete
                    viewModelScope.launch {
                        completeCycle()
                    }
                    current.copy(
                        state = MachineState.COMPLETED,
                        stageElapsedSeconds = current.stageDurationSeconds,
                        totalElapsedSeconds = current.totalDurationSeconds,
                        motorRpm = 0,
                        leftBladeRpm = 0,
                        rightBladeRpm = 0,
                        heatingElementActive = false,
                        baseHeaterActive = false,
                        lidHeaterActive = false
                    )
                }
            } else {
                current.copy(
                    stageElapsedSeconds = newStageElapsed,
                    totalElapsedSeconds = newTotalElapsed,
                    chamberTempF = newChamberTemp,
                    motorRpm = currentMotorRpm,
                    leftBladeRpm = leftBlade,
                    rightBladeRpm = rightBlade,
                    heatingElementActive = isHeating,
                    baseHeaterActive = baseHeater,
                    lidHeaterActive = lidHeater
                )
            }
        }
    }

    private fun transitionToStage(index: Int) {
        val current = _telemetry.value
        val recipe = current.activeRecipe ?: return
        val targetStage = recipe.stages.getOrNull(index) ?: return

        // Calculate total elapsed up to start of this stage
        var priorElapsed = 0
        for (i in 0 until index) {
            priorElapsed += recipe.stages[i].durationMinutes * 60
        }

        _telemetry.update { curr ->
            val updated = curr.copy(
                currentStageIndex = index,
                stageElapsedSeconds = 0,
                stageDurationSeconds = targetStage.durationMinutes * 60,
                totalElapsedSeconds = priorElapsed,
                activeAlert = null
            )
            recalculateTargetTemp(updated)
        }
        checkForStageAlert(index, isStageStart = true)
    }

    private fun checkForStageAlert(stageIndex: Int, isStageStart: Boolean) {
        val current = _telemetry.value
        val recipe = current.activeRecipe ?: return
        val stage = recipe.stages.getOrNull(stageIndex) ?: return

        if (stage.hasIngredientAlert && isStageStart) {
            val alertTitle = stage.alertTitle ?: "Ingredient Alert"
            val alertDesc = stage.alertDescription ?: "Add ingredients for ${stage.title}"

            // Filter ingredients assigned to this stage
            val itemsForStage = recipe.ingredients
                .filter { it.stageToAdd == stage.type }
                .map { "${it.amount} ${it.name}" }

            val alert = IngredientAlert(
                title = alertTitle,
                message = alertDesc,
                stageType = stage.type,
                itemsToAdd = itemsForStage
            )

            _telemetry.update { it.copy(activeAlert = alert) }
            notificationHelper.sendIngredientAlert(
                title = alertTitle,
                message = alertDesc,
                stageName = stage.title,
                itemsToAdd = itemsForStage
            )
        } else if (stage.type == BakingStageType.PUNCH_DOWN && current.paddleRemovalReminderEnabled) {
            val isZojirushi = current.model == BreadMakerModel.ZOJIRUSHI_BB_PDC20
            val alert = IngredientAlert(
                title = if (isZojirushi) "Zojirushi BB-PDC20: Remove Twin Paddles" else "Paddle Extraction Reminder",
                message = if (isZojirushi) {
                    "Extract both kneading blades (Left & Right paddles) from the horizontal pan before final proof to avoid holes in your 2-lb loaf."
                } else {
                    "Extract the metal kneading paddle now so the loaf bakes without a bottom hole."
                },
                stageType = stage.type,
                itemsToAdd = emptyList()
            )
            _telemetry.update { it.copy(activeAlert = alert) }
            notificationHelper.sendIngredientAlert(
                title = alert.title,
                message = alert.message,
                stageName = stage.title,
                itemsToAdd = emptyList()
            )
        }
    }

    private fun completeCycle() {
        tickerJob?.cancel()
        tickerJob = null
        val recipeTitle = _telemetry.value.activeRecipe?.title ?: "Loaf"
        notificationHelper.sendBakeCompleteNotification(recipeTitle)
        _telemetry.update {
            it.copy(
                state = MachineState.COMPLETED,
                motorRpm = 0,
                heatingElementActive = false,
                activeAlert = IngredientAlert(
                    title = "Baking Finished! 🎉",
                    message = "Your golden crust bread is ready. Remove the pan within 15 minutes to preserve crispy crust texture.",
                    stageType = BakingStageType.KEEP_WARM,
                    itemsToAdd = emptyList()
                )
            )
        }
        showFeedback("🎉 Baking complete! Remove loaf from pan.")
    }

    private fun recalculateTargetTemp(telemetry: MachineTelemetry): MachineTelemetry {
        val stage = telemetry.activeRecipe?.stages?.getOrNull(telemetry.currentStageIndex)
        if (stage == null) {
            return telemetry.copy(chamberTargetTempF = 75f)
        }

        var baseTarget = stage.targetTempF.toFloat()
        // Apply crust offsets and user adjustments
        if (stage.type == BakingStageType.BAKE) {
            baseTarget += telemetry.selectedCrust.bakeTempOffset
            baseTarget += telemetry.bakeTempOffsetF
            if (telemetry.crustBoosterActive) {
                baseTarget += 20f
            }
        } else if (stage.type in listOf(BakingStageType.RISE_1, BakingStageType.RISE_2, BakingStageType.RISE_3)) {
            baseTarget += telemetry.proofTempOffsetF
        }

        return telemetry.copy(chamberTargetTempF = baseTarget)
    }

    // Custom Recipe Creation & Management (Room Database)
    fun saveCustomRecipe(recipe: BreadRecipe) {
        viewModelScope.launch {
            repository.saveCustomRecipe(recipe)
            showFeedback("Saved custom recipe \"${recipe.title}\" to Room database")
        }
    }

    fun deleteCustomRecipe(recipeId: String) {
        viewModelScope.launch {
            repository.deleteCustomRecipe(recipeId)
            showFeedback("Deleted custom recipe from Room database")
        }
    }

    // ==========================================
    // Bread Stage Countdown Timer Operations
    // ==========================================

    fun selectTimerRoutine(routine: TimerRoutine) {
        stageCountdownJob?.cancel()
        stageCountdownJob = null
        _stageTimersState.update {
            it.copy(
                activeRoutine = routine,
                currentStageIndex = 0,
                isRunning = false,
                completedAlert = null
            )
        }
        showFeedback("Loaded schedule: ${routine.title}")
    }

    fun selectStage(index: Int) {
        val count = _stageTimersState.value.activeRoutine.stages.size
        if (index in 0 until count) {
            _stageTimersState.update { it.copy(currentStageIndex = index) }
        }
    }

    fun startStageCountdown() {
        if (_stageTimersState.value.isRunning) return
        _stageTimersState.update { it.copy(isRunning = true) }
        startStageCountdownTicker()
        val stageName = _stageTimersState.value.currentStage?.name ?: "Stage"
        showFeedback("Started countdown: $stageName")
    }

    fun pauseStageCountdown() {
        stageCountdownJob?.cancel()
        stageCountdownJob = null
        _stageTimersState.update { it.copy(isRunning = false) }
        showFeedback("Timer paused")
    }

    fun resumeStageCountdown() {
        startStageCountdown()
    }

    fun resetCurrentStageCountdown() {
        stageCountdownJob?.cancel()
        stageCountdownJob = null
        _stageTimersState.update { state ->
            val active = state.currentStage ?: return@update state
            val updated = state.activeRoutine.stages.toMutableList()
            updated[state.currentStageIndex] = active.copy(
                remainingSeconds = active.targetDurationSeconds,
                isRunning = false,
                isCompleted = false
            )
            state.copy(
                activeRoutine = state.activeRoutine.copy(stages = updated),
                isRunning = false
            )
        }
        showFeedback("Reset timer to original duration")
    }

    fun adjustCurrentStageCountdown(deltaSeconds: Int) {
        _stageTimersState.update { state ->
            val active = state.currentStage ?: return@update state
            val newRemaining = (active.remainingSeconds + deltaSeconds).coerceAtLeast(0)
            val updated = state.activeRoutine.stages.toMutableList()
            updated[state.currentStageIndex] = active.copy(remainingSeconds = newRemaining)
            state.copy(activeRoutine = state.activeRoutine.copy(stages = updated))
        }
        val mins = deltaSeconds / 60
        showFeedback(if (deltaSeconds > 0) "+$mins min added" else "$mins min adjusted")
    }

    fun setStageDuration(stageIndex: Int, newSeconds: Int) {
        val clamped = newSeconds.coerceIn(10, 86400) // 10s to 24h
        _stageTimersState.update { state ->
            if (stageIndex !in state.activeRoutine.stages.indices) return@update state
            val updated = state.activeRoutine.stages.toMutableList()
            val old = updated[stageIndex]
            updated[stageIndex] = old.copy(
                targetDurationSeconds = clamped,
                remainingSeconds = clamped,
                isCompleted = false
            )
            state.copy(activeRoutine = state.activeRoutine.copy(stages = updated))
        }
        showFeedback("Updated timer target duration")
    }

    fun skipToNextStageTimer() {
        _stageTimersState.update { state ->
            val nextIndex = state.currentStageIndex + 1
            if (nextIndex < state.activeRoutine.stages.size) {
                state.copy(currentStageIndex = nextIndex, completedAlert = null)
            } else state
        }
        val stageName = _stageTimersState.value.currentStage?.name ?: "Next Stage"
        showFeedback("Advanced to: $stageName")
    }

    fun previousStageTimer() {
        _stageTimersState.update { state ->
            val prevIndex = (state.currentStageIndex - 1).coerceAtLeast(0)
            state.copy(currentStageIndex = prevIndex, completedAlert = null)
        }
    }

    fun addCustomStageTimer(name: String, category: TimerStageCategory, durationSeconds: Int, tips: String) {
        val newStage = BreadStageTimer(
            name = name.ifBlank { "Custom Stage" },
            category = category,
            targetDurationSeconds = durationSeconds.coerceAtLeast(30),
            bakerTips = tips
        )
        _stageTimersState.update { state ->
            val updated = state.activeRoutine.stages + newStage
            state.copy(activeRoutine = state.activeRoutine.copy(stages = updated))
        }
        showFeedback("Added \"${newStage.name}\" to timer routine")
    }

    fun deleteStageTimer(stageIndex: Int) {
        _stageTimersState.update { state ->
            if (state.activeRoutine.stages.size <= 1) {
                return@update state // Keep at least one stage
            }
            val updated = state.activeRoutine.stages.toMutableList()
            updated.removeAt(stageIndex)
            val newIndex = state.currentStageIndex.coerceAtMost(updated.size - 1)
            state.copy(activeRoutine = state.activeRoutine.copy(stages = updated), currentStageIndex = newIndex)
        }
        showFeedback("Removed stage timer")
    }

    fun dismissStageTimerAlert() {
        _stageTimersState.update { it.copy(completedAlert = null) }
    }

    private fun startStageCountdownTicker() {
        stageCountdownJob?.cancel()
        stageCountdownJob = viewModelScope.launch {
            while (isActive && _stageTimersState.value.isRunning) {
                delay(1000L)
                val current = _stageTimersState.value
                val activeStage = current.currentStage
                if (activeStage == null) {
                    _stageTimersState.update { it.copy(isRunning = false) }
                    break
                }

                val newRemaining = activeStage.remainingSeconds - 1
                if (newRemaining <= 0) {
                    val completedStage = activeStage.copy(remainingSeconds = 0, isCompleted = true, isRunning = false)
                    val updatedStages = current.activeRoutine.stages.toMutableList()
                    updatedStages[current.currentStageIndex] = completedStage
                    val nextIndex = current.currentStageIndex + 1
                    val hasNext = nextIndex < updatedStages.size
                    val nextStage = if (hasNext) updatedStages[nextIndex] else null

                    notificationHelper.sendStepCompleteNotification(
                        completedStageName = completedStage.name,
                        nextStageName = nextStage?.name,
                        guidance = "${completedStage.name} is complete! ${completedStage.bakerTips}",
                        isProofingStep = completedStage.category in listOf(
                            TimerStageCategory.BULK_PROOF,
                            TimerStageCategory.FINAL_PROOF,
                            TimerStageCategory.AUTOLYSE,
                            TimerStageCategory.BENCH_REST
                        )
                    )

                    _stageTimersState.update { state ->
                        state.copy(
                            activeRoutine = state.activeRoutine.copy(stages = updatedStages),
                            currentStageIndex = if (hasNext) nextIndex else state.currentStageIndex,
                            isRunning = false,
                            completedAlert = completedStage
                        )
                    }
                    showFeedback("⏰ Stage complete: ${completedStage.name}!")
                    break
                } else {
                    val updatedStages = current.activeRoutine.stages.toMutableList()
                    updatedStages[current.currentStageIndex] = activeStage.copy(
                        remainingSeconds = newRemaining,
                        isRunning = true
                    )
                    _stageTimersState.update {
                        it.copy(activeRoutine = it.activeRoutine.copy(stages = updatedStages))
                    }
                }
            }
        }
    }
}
