package com.example.data.model

enum class BreadMakerModel(
    val modelName: String,
    val shortName: String,
    val panType: String,
    val bladeCount: Int,
    val hasLidHeater: Boolean,
    val hasHomemadeMemory: Boolean,
    val defaultLoafCapacity: String
) {
    ZOJIRUSHI_BB_PDC20(
        modelName = "Zojirushi Home Bakery Virtuoso® Plus (BB-PDC20)",
        shortName = "Zojirushi BB-PDC20",
        panType = "Traditional 2.0 lb Horizontal Dual-Coupling Pan",
        bladeCount = 2,
        hasLidHeater = true,
        hasHomemadeMemory = true,
        defaultLoafCapacity = "2.0 lb Rectangular Loaf"
    ),
    UNIVERSAL_SMART_BAKER(
        modelName = "CrustMaster Pro Smart Baker",
        shortName = "CrustMaster Pro",
        panType = "Standard 1.5 - 2.0 lb Pan",
        bladeCount = 1,
        hasLidHeater = false,
        hasHomemadeMemory = false,
        defaultLoafCapacity = "1.5 - 2.0 lb Loaf"
    )
}

enum class ZojirushiMenuCourse(val courseNumber: Int, val courseName: String, val description: String) {
    WHITE(1, "White", "Classic sandwich loaf with soft crumb"),
    WHOLE_WHEAT(2, "Whole Wheat", "100% whole grain with extended preheat and hydration"),
    EUROPEAN(3, "European", "Crusty artisan loaf with crisp top crust via lid heater"),
    MULTIGRAIN(4, "Multigrain", "Hearty grains with auto-dispenser seed and kernel addition"),
    GLUTEN_FREE(5, "Gluten Free", "Single intensive mix and high-heat starch bake"),
    SALT_FREE(6, "Salt Free", "Low sodium formula with tailored yeast activity"),
    SUGAR_FREE(7, "Sugar Free", "Extended proof for natural flour sweetness"),
    VEGAN(8, "Vegan", "Plant-based milk and dairy-free enrichment"),
    QUICK_WHITE(9, "Quick White", "Rapid cycle with instant yeast in under 2 hours"),
    QUICK_WHOLE_WHEAT(10, "Quick Whole Wheat", "Accelerated whole grain cycle"),
    DOUGH(11, "Dough", "Knead and proof cycle for shaping and oven baking"),
    SOURDOUGH_STARTER(12, "Sourdough Starter", "Precise 82°F chamber temperature for wild yeast culturing"),
    CAKE_JAM(13, "Cake / Jam", "Stirring and steady simmer heating"),
    HOMEMADE_CUSTOM(14, "Homemade Custom", "Fully programmable stages stored in memory slots")
}

enum class BakingStageType(val displayName: String, val shortCode: String) {
    PREHEAT("Preheat & Temp Equalize", "PRE"),
    KNEAD_1("Hydration & Flour Mixing", "KN1"),
    REST_AUTOLYSE("Rest & Autolyse", "RST"),
    KNEAD_2("Gluten Development & Add-in", "KN2"),
    RISE_1("Bulk Fermentation", "RS1"),
    PUNCH_DOWN("Degas & Paddle Removal", "PCH"),
    RISE_2("Intermediate Proof", "RS2"),
    RISE_3("Final Proof & Loaf Rise", "RS3"),
    BAKE("Artisan Crust Bake", "BAK"),
    KEEP_WARM("Crust Preservation Warm", "WRM")
}

enum class CrustShade(val label: String, val bakeTempOffset: Int, val description: String) {
    LIGHT("Light", -15, "Delicate golden, soft tender thin crust"),
    MEDIUM("Medium", 0, "Classic balanced golden-brown crust"),
    DARK("Dark", +15, "Deep caramel, pronounced roasted notes"),
    ARTISAN_CRISPY("Artisan Crispy", +25, "Extra crisp blistered crust with top element boost")
}

enum class LoafSize(val label: String, val weightGrams: Int, val flourMultiplier: Float) {
    ONE_LB("1.0 lb", 450, 0.75f),
    ONE_HALF_LB("1.5 lb", 680, 1.0f),
    TWO_LB("2.0 lb", 900, 1.33f)
}

enum class YeastType(val label: String, val bloomNeeded: Boolean, val fermentationSpeed: String) {
    SOURDOUGH_STARTER("Active Sourdough Levain (100% Hydration)", false, "Slow & complex natural fermentation"),
    INSTANT_YEAST("Instant / Bread Machine Yeast", false, "Fast direct mix into dry ingredients"),
    ACTIVE_DRY_YEAST("Active Dry Yeast", true, "Requires 10-min lukewarm liquid activation"),
    FRESH_CAKE_YEAST("Fresh Baker's Compressed Yeast", true, "Artisanal aroma, crumble into liquid")
}

enum class FlourCategory(val label: String, val iconName: String) {
    ALL("All Recipes", "all"),
    CUSTOM("Custom Recipes", "custom"),
    SOURDOUGH("Wild Sourdough", "sourdough"),
    SPELT_ANCIENT("Spelt & Ancient Grains", "spelt"),
    DARK_RYE("Dark Rye & Roggenbrot", "rye"),
    WHOLE_WHEAT("Stoneground Whole Wheat", "whole_wheat"),
    BRIOCHE_SWEET("Brioche & Enriched", "brioche"),
    GLUTEN_FREE("Gluten-Free Artisan", "gluten_free"),
    SPECIALTY("Shokupan & Milk Bread", "specialty")
}

enum class MachineState {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

data class RecipeIngredient(
    val id: String,
    val name: String,
    val amount: String,
    val bakerPercentage: Float = 0f,
    val stageToAdd: BakingStageType = BakingStageType.PREHEAT,
    val isAutoDispenser: Boolean = false,
    val notes: String = ""
)

data class BakingStage(
    val type: BakingStageType,
    val title: String,
    val durationMinutes: Int,
    val targetTempF: Int,
    val heatingPowerPercent: Int = 0,
    val motorRpm: Int = 0,
    val hasIngredientAlert: Boolean = false,
    val alertTitle: String? = null,
    val alertDescription: String? = null,
    val chimePrompt: String? = null
)

data class BreadRecipe(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: FlourCategory,
    val primaryFlour: String,
    val specialtyFlours: List<String> = emptyList(),
    val yeastType: YeastType,
    val defaultLoafSize: LoafSize = LoafSize.TWO_LB,
    val defaultCrust: CrustShade = CrustShade.MEDIUM,
    val hydrationPercent: Int, // e.g. 68%
    val totalTimeMinutes: Int,
    val ingredients: List<RecipeIngredient>,
    val stages: List<BakingStage>,
    val bakerNotes: List<String> = emptyList(),
    val isCustom: Boolean = false,
    val zojirushiRecommendedCourse: ZojirushiMenuCourse = ZojirushiMenuCourse.EUROPEAN
)

data class IngredientAlert(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val message: String,
    val stageType: BakingStageType,
    val itemsToAdd: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class StepCompletionAlert(
    val id: String = System.currentTimeMillis().toString(),
    val completedStage: BakingStage,
    val nextStage: BakingStage?,
    val isProofing: Boolean,
    val isBaking: Boolean,
    val title: String,
    val message: String,
    val actionPrompt: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MachineTelemetry(
    val model: BreadMakerModel = BreadMakerModel.ZOJIRUSHI_BB_PDC20,
    val isConnected: Boolean = true,
    val machineName: String = "Zojirushi Virtuoso® Plus BB-PDC20",
    val connectionType: String = "Bluetooth 5.2 LE (Zojirushi Connect)",
    val signalDbm: Int = -54,
    val firmwareVersion: String = "v4.1.2-bbpdc20",
    val state: MachineState = MachineState.IDLE,
    val activeRecipe: BreadRecipe? = null,
    val currentStageIndex: Int = 0,
    val stageElapsedSeconds: Int = 0,
    val stageDurationSeconds: Int = 0,
    val totalElapsedSeconds: Int = 0,
    val totalDurationSeconds: Int = 0,
    val chamberTempF: Float = 75f,
    val chamberTargetTempF: Float = 75f,
    val proofTempOffsetF: Int = 0, // +/- 10°F user adjustment
    val bakeTempOffsetF: Int = 0,  // +/- 30°F user adjustment
    val crustBoosterActive: Boolean = false, // +20°F radiant top heat in last 10 mins
    val selectedCrust: CrustShade = CrustShade.MEDIUM,
    val selectedLoafSize: LoafSize = LoafSize.TWO_LB,
    val lidOpen: Boolean = false,
    val panLocked: Boolean = true,
    val interiorLampOn: Boolean = false,
    val motorRpm: Int = 0,
    // Zojirushi BB-PDC20 specific hardware telemetry:
    val leftBladeRpm: Int = 0,
    val rightBladeRpm: Int = 0,
    val lidHeaterActive: Boolean = false,  // Upper lid heater for even browning
    val baseHeaterActive: Boolean = false, // Lower base heater
    val heatingElementActive: Boolean = false,
    val zojirushiMenuCourse: ZojirushiMenuCourse = ZojirushiMenuCourse.EUROPEAN,
    val homemadeMemorySlot: Int = 1, // Memory 1, 2, or 3
    val paddleRemoved: Boolean = false,
    val twinPaddlesRemoved: Boolean = false,
    val paddleRemovalReminderEnabled: Boolean = true,
    val simulationSpeed: Int = 1, // 1x normal, 10x, 30x fast demo
    val activeAlert: IngredientAlert? = null,
    val activeStepCompletionAlert: StepCompletionAlert? = null,
    val delayStartMinutes: Int = 0,
    val converterSettings: ConverterSettings = ConverterSettings()
) {
    val currentStage: BakingStage?
        get() = activeRecipe?.stages?.getOrNull(currentStageIndex)

    val isCurrentStageProofing: Boolean
        get() = currentStage?.type in listOf(BakingStageType.RISE_1, BakingStageType.RISE_2, BakingStageType.RISE_3, BakingStageType.REST_AUTOLYSE)

    val isCurrentStageBaking: Boolean
        get() = currentStage?.type == BakingStageType.BAKE

    val progressFraction: Float
        get() = if (totalDurationSeconds > 0) {
            (totalElapsedSeconds.toFloat() / totalDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val stageProgressFraction: Float
        get() = if (stageDurationSeconds > 0) {
            (stageElapsedSeconds.toFloat() / stageDurationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val remainingSeconds: Int
        get() = (totalDurationSeconds - totalElapsedSeconds).coerceAtLeast(0)

    val stageRemainingSeconds: Int
        get() = (stageDurationSeconds - stageElapsedSeconds).coerceAtLeast(0)
}
