package com.example.data.model

/**
 * Detailed representation of automatic bread machine cycles (courses),
 * including each internal cycle stage (preheat, kneading, rest, proofs, bake, keep warm),
 * temperature profiles, motor RPMs, and alert chimes.
 */
data class BreadMachineCycle(
    val id: String,
    val courseNumber: Int,
    val name: String,
    val shortName: String,
    val description: String,
    val emoji: String,
    val totalMinutes: Int,
    val course: ZojirushiMenuCourse,
    val category: RecipeCategory,
    val stages: List<BakingStage>,
    val defaultCrust: CrustShade = CrustShade.MEDIUM,
    val defaultLoafSize: LoafSize = LoafSize.TWO_LB,
    val hasLidHeater: Boolean = true,
    val paddleRemovalStageIndex: Int? = null,
    val dispenserAlertStageIndex: Int? = null,
    val tips: String = ""
) {
    val totalDurationSeconds: Int = stages.sumOf { it.durationMinutes * 60 }
}

object PreloadedBreadMachineCycles {

    // 1. Basic White Bread Cycle
    val WHITE_BREAD = BreadMachineCycle(
        id = "cycle_white_bread",
        courseNumber = 1,
        name = "Basic White Bread Cycle",
        shortName = "White Loaf",
        description = "Standard 2-lb horizontal loaf cycle with twin-blade gluten development and balanced browning.",
        emoji = "🍞",
        totalMinutes = 225, // 3 hours 45 minutes
        course = ZojirushiMenuCourse.WHITE,
        category = RecipeCategory.YEAST_BREAD,
        defaultCrust = CrustShade.MEDIUM,
        defaultLoafSize = LoafSize.TWO_LB,
        paddleRemovalStageIndex = 5,
        dispenserAlertStageIndex = 3,
        tips = "Standard sandwich cycle. Extract blades at the double beep to avoid holes in the loaf bottom.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "Preheat & Temp Equalize", 20, 80, 10, 0),
            BakingStage(BakingStageType.KNEAD_1, "Initial Hydration Mix", 10, 80, 0, 50),
            BakingStage(BakingStageType.REST_AUTOLYSE, "Rest & Moisture Absorption", 10, 80, 0, 0),
            BakingStage(
                BakingStageType.KNEAD_2,
                "Twin-Blade Vigorous Knead",
                17,
                82,
                0,
                115,
                hasIngredientAlert = true,
                alertTitle = "Dispenser Chime: Add Ingredients",
                alertDescription = "Chime sounds 15 minutes into Knead 2 for raisins, seeds, or nuts."
            ),
            BakingStage(BakingStageType.RISE_1, "First Bulk Fermentation", 45, 84, 20, 0),
            BakingStage(
                BakingStageType.PUNCH_DOWN,
                "Paddle Removal & De-gas",
                3,
                84,
                0,
                30,
                hasIngredientAlert = true,
                alertTitle = "Paddle Extraction Alert",
                alertDescription = "Remove both twin blades from pan before final rises."
            ),
            BakingStage(BakingStageType.RISE_2, "Intermediate Rest & Rise", 35, 86, 25, 0),
            BakingStage(BakingStageType.RISE_3, "Final Loaf Crown Proof", 35, 90, 30, 0),
            BakingStage(BakingStageType.BAKE, "Dual-Heater Hearth Bake", 40, 375, 90, 0),
            BakingStage(BakingStageType.KEEP_WARM, "Crust Crisp Cooling Vent", 10, 140, 10, 0)
        )
    )

    // 2. 100% Whole Wheat Cycle
    val WHOLE_WHEAT = BreadMachineCycle(
        id = "cycle_whole_wheat",
        courseNumber = 2,
        name = "100% Whole Wheat Cycle",
        shortName = "Whole Wheat",
        description = "Extended preheat cycle to soften coarse wheat bran and hydrate heavy whole grains.",
        emoji = "🌾",
        totalMinutes = 225,
        course = ZojirushiMenuCourse.WHOLE_WHEAT,
        category = RecipeCategory.WHOLE_GRAIN,
        defaultCrust = CrustShade.MEDIUM,
        defaultLoafSize = LoafSize.TWO_LB,
        paddleRemovalStageIndex = 5,
        tips = "Extended 35-min preheat hydrates the fibrous bran, leading to maximum loft without bitterness.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "Extended Bran Hydration Preheat", 35, 84, 15, 0),
            BakingStage(BakingStageType.KNEAD_1, "Whole Grain Cohesion Mix", 12, 84, 0, 50),
            BakingStage(BakingStageType.REST_AUTOLYSE, "Gluten Conditioning Rest", 10, 84, 0, 0),
            BakingStage(BakingStageType.KNEAD_2, "Twin-Blade Elasticity Knead", 20, 84, 0, 110),
            BakingStage(BakingStageType.RISE_1, "Whole Grain Bulk Rise", 45, 86, 20, 0),
            BakingStage(
                BakingStageType.PUNCH_DOWN,
                "Paddle Removal Chime",
                3,
                86,
                0,
                30,
                hasIngredientAlert = true,
                alertTitle = "Extract Twin Paddles",
                alertDescription = "Remove blades so whole wheat loaf releases intact."
            ),
            BakingStage(BakingStageType.RISE_2, "Intermediate Rise", 35, 88, 25, 0),
            BakingStage(BakingStageType.RISE_3, "Final Proofing", 35, 90, 30, 0),
            BakingStage(BakingStageType.BAKE, "Deep Roast Bake (Lid Booster)", 45, 380, 95, 0),
            BakingStage(BakingStageType.KEEP_WARM, "Warm Cool Down", 15, 140, 10, 0)
        )
    )

    // 3. European / Artisan Sourdough Cycle
    val EUROPEAN_SOURDOUGH = BreadMachineCycle(
        id = "cycle_european_sourdough",
        courseNumber = 3,
        name = "European / Artisan Sourdough Cycle",
        shortName = "Sourdough Artisan",
        description = "Lean dough cycle utilizing the upper lid radiant heater for a crackling, blistered crust.",
        emoji = "🥖",
        totalMinutes = 225,
        course = ZojirushiMenuCourse.EUROPEAN,
        category = RecipeCategory.SOURDOUGH,
        defaultCrust = CrustShade.ARTISAN_CRISPY,
        defaultLoafSize = LoafSize.TWO_LB,
        paddleRemovalStageIndex = 5,
        dispenserAlertStageIndex = 3,
        tips = "Lid heater engages at high wattage for blistered hearth browning. Ideal for wild sourdough levain.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "European Cold Equalization", 20, 80, 10, 0),
            BakingStage(BakingStageType.KNEAD_1, "Lean Dough Hydration Mix", 10, 80, 0, 50),
            BakingStage(BakingStageType.REST_AUTOLYSE, "Autolyse Enzyme Rest", 15, 80, 0, 0),
            BakingStage(
                BakingStageType.KNEAD_2,
                "Intensive Gluten Extensibility",
                25,
                80,
                0,
                120,
                hasIngredientAlert = true,
                alertTitle = "Add Mix-ins Chime",
                alertDescription = "Add olives, walnuts, or rosemary during this phase."
            ),
            BakingStage(BakingStageType.RISE_1, "Slow Wild Levain Ferment", 45, 82, 20, 0),
            BakingStage(
                BakingStageType.PUNCH_DOWN,
                "Paddle Removal Beep",
                3,
                82,
                0,
                30,
                hasIngredientAlert = true,
                alertTitle = "Extract Twin Blades",
                alertDescription = "Remove blades before 70 minutes of final proofing."
            ),
            BakingStage(BakingStageType.RISE_2, "Intermediate Rest", 35, 86, 25, 0),
            BakingStage(BakingStageType.RISE_3, "Final Crust Rise & Score", 35, 88, 30, 0),
            BakingStage(BakingStageType.BAKE, "Intense Dual-Heater Bake", 50, 395, 100, 0),
            BakingStage(BakingStageType.KEEP_WARM, "Moisture Evaporate Hold", 15, 140, 10, 0)
        )
    )

    // 4. Multigrain & Seeded Cycle
    val MULTIGRAIN = BreadMachineCycle(
        id = "cycle_multigrain",
        courseNumber = 4,
        name = "Multigrain & Seeded Cycle",
        shortName = "Multigrain",
        description = "Designed for 7-grain blends with automatic seed dispenser chime during second knead.",
        emoji = "🌻",
        totalMinutes = 230,
        course = ZojirushiMenuCourse.MULTIGRAIN,
        category = RecipeCategory.WHOLE_GRAIN,
        defaultCrust = CrustShade.MEDIUM,
        defaultLoafSize = LoafSize.TWO_LB,
        paddleRemovalStageIndex = 5,
        dispenserAlertStageIndex = 3,
        tips = "Chime notifies when to add delicate sunflower, pumpkin, and sesame seeds so they aren't crushed.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "Grain Tempering Equalize", 25, 80, 10, 0),
            BakingStage(BakingStageType.KNEAD_1, "Multi-Flour Incorporation", 10, 80, 0, 50),
            BakingStage(BakingStageType.REST_AUTOLYSE, "Seed Hydration Rest", 15, 80, 0, 0),
            BakingStage(
                BakingStageType.KNEAD_2,
                "Seed Dispersion Knead",
                22,
                82,
                0,
                105,
                hasIngredientAlert = true,
                alertTitle = "Dispenser Chime: Add Seed Blend",
                alertDescription = "Automatic dispenser drops seeds into dough."
            ),
            BakingStage(BakingStageType.RISE_1, "Bulk Grain Fermentation", 45, 84, 20, 0),
            BakingStage(BakingStageType.PUNCH_DOWN, "Paddle Extraction Beep", 3, 84, 0, 30),
            BakingStage(BakingStageType.RISE_2, "Intermediate Rise", 35, 86, 25, 0),
            BakingStage(BakingStageType.RISE_3, "Final Crown Proof", 35, 90, 30, 0),
            BakingStage(BakingStageType.BAKE, "Hearty Grain Hearth Bake", 50, 380, 90, 0),
            BakingStage(BakingStageType.KEEP_WARM, "Warm Cool Vent", 15, 140, 10, 0)
        )
    )

    // 5. Gluten-Free Batter Cycle
    val GLUTEN_FREE = BreadMachineCycle(
        id = "cycle_gluten_free",
        courseNumber = 5,
        name = "Gluten-Free Quick Batter Cycle",
        shortName = "Gluten-Free",
        description = "High-speed batter whip with single continuous rise and immediate high-temperature bake.",
        emoji = "🌿",
        totalMinutes = 145, // 2 hours 25 minutes
        course = ZojirushiMenuCourse.GLUTEN_FREE,
        category = RecipeCategory.GLUTEN_FREE,
        defaultCrust = CrustShade.MEDIUM,
        defaultLoafSize = LoafSize.TWO_LB,
        paddleRemovalStageIndex = null,
        tips = "Gluten-free requires no multi-punch downs. Do not open lid during the 55-minute single rise.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "Chamber Warmup (85°F)", 15, 85, 15, 0),
            BakingStage(BakingStageType.KNEAD_1, "Twin-Blade High-Speed Batter Whip", 25, 85, 0, 130),
            BakingStage(BakingStageType.RISE_1, "Single Stage Continuous Rise", 55, 92, 35, 0),
            BakingStage(BakingStageType.BAKE, "High-Heat Starch Bake (Base + Lid)", 50, 385, 95, 0)
        )
    )

    // 6. Sweet Bread & Brioche Cycle
    val SWEET_BRIOCHE = BreadMachineCycle(
        id = "cycle_sweet_brioche",
        courseNumber = 8,
        name = "Sweet Bread & Brioche Cycle",
        shortName = "Sweet Loaf",
        description = "Gentle thermal control for high-sugar, butter, and egg doughs to prevent scorching.",
        emoji = "🍯",
        totalMinutes = 210, // 3 hours 30 minutes
        course = ZojirushiMenuCourse.WHITE,
        category = RecipeCategory.SWEET_LOAVES,
        defaultCrust = CrustShade.LIGHT,
        defaultLoafSize = LoafSize.ONE_HALF_LB,
        paddleRemovalStageIndex = 5,
        dispenserAlertStageIndex = 3,
        tips = "Add cold butter at the Knead 2 chime for supreme featherweight flakiness. Use Light crust setting.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "Egg & Dairy Tempering", 10, 76, 0, 0),
            BakingStage(BakingStageType.KNEAD_1, "Initial Flour Cohesion", 12, 76, 0, 70),
            BakingStage(BakingStageType.REST_AUTOLYSE, "Dairy Hydration Rest", 10, 76, 0, 0),
            BakingStage(
                BakingStageType.KNEAD_2,
                "Cold Butter Emulsification Knead",
                25,
                78,
                0,
                120,
                hasIngredientAlert = true,
                alertTitle = "Add Chilled Diced Butter",
                alertDescription = "Drop in cold butter cubes 2-3 at a time."
            ),
            BakingStage(BakingStageType.RISE_1, "Enriched Low-Temp Bulk Rise", 55, 80, 15, 0),
            BakingStage(BakingStageType.PUNCH_DOWN, "De-air & Extract Paddles", 3, 80, 0, 25),
            BakingStage(BakingStageType.RISE_2, "Rest & Crown Formation", 35, 82, 20, 0),
            BakingStage(BakingStageType.RISE_3, "Final Brioche Proof & Glaze", 25, 84, 20, 0),
            BakingStage(BakingStageType.BAKE, "Gentle Golden Caramel Bake", 40, 340, 75, 0),
            BakingStage(BakingStageType.KEEP_WARM, "Moisture Retention Hold", 10, 130, 10, 0)
        )
    )

    // 7. Quick / Rapid White Bread Cycle
    val QUICK_RAPID = BreadMachineCycle(
        id = "cycle_quick_white",
        courseNumber = 9,
        name = "Quick Rapid White Cycle",
        shortName = "Rapid Express",
        description = "Accelerated loaf cycle baking fresh bread in under 2 hours using rapid-rise yeast.",
        emoji = "⚡",
        totalMinutes = 118, // 1 hour 58 minutes
        course = ZojirushiMenuCourse.QUICK_WHITE,
        category = RecipeCategory.YEAST_BREAD,
        defaultCrust = CrustShade.MEDIUM,
        defaultLoafSize = LoafSize.TWO_LB,
        paddleRemovalStageIndex = null,
        tips = "Fastest turnaround. Use double the rapid-rise yeast and water at 100°F–110°F.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "Rapid Warmup & Equalize", 10, 84, 15, 0),
            BakingStage(BakingStageType.KNEAD_1, "Intensive High-Speed Mix", 20, 84, 0, 130),
            BakingStage(BakingStageType.RISE_1, "Accelerated Proof (88°F)", 43, 88, 30, 0),
            BakingStage(BakingStageType.BAKE, "Turbo Dual-Heater Bake", 45, 385, 95, 0)
        )
    )

    // 8. Artisan Pizza & Bread Dough Cycle
    val DOUGH_CYCLE = BreadMachineCycle(
        id = "cycle_dough",
        courseNumber = 11,
        name = "Artisan Pizza & Bread Dough Cycle",
        shortName = "Dough Mode",
        description = "Kneads and ferments dough to windowpane elasticity, ready for hand shaping and stone baking.",
        emoji = "🍕",
        totalMinutes = 110, // 1 hour 50 minutes
        course = ZojirushiMenuCourse.DOUGH,
        category = RecipeCategory.YEAST_BREAD,
        defaultCrust = CrustShade.MEDIUM,
        defaultLoafSize = LoafSize.TWO_LB,
        paddleRemovalStageIndex = null,
        tips = "No baking occurs. When cycle chimes, dough is relaxed and ready for rolling or baguette shaping.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "Water & Chamber Tempering", 15, 82, 10, 0),
            BakingStage(BakingStageType.KNEAD_1, "Initial Hydration Mix", 10, 82, 0, 60),
            BakingStage(BakingStageType.REST_AUTOLYSE, "Gluten Rest", 10, 82, 0, 0),
            BakingStage(BakingStageType.KNEAD_2, "Twin Blade Windowpane Knead", 25, 82, 0, 120),
            BakingStage(BakingStageType.RISE_1, "Bulk Rise & Gluten Relaxation", 50, 86, 25, 0)
        )
    )

    // 9. Sourdough Starter Culturing Cycle
    val SOURDOUGH_STARTER = BreadMachineCycle(
        id = "cycle_sourdough_starter",
        courseNumber = 12,
        name = "Sourdough Starter Culture Cycle",
        shortName = "Starter 82°F",
        description = "Maintains precise thermostatically controlled 82°F environment for culturing wild starters.",
        emoji = "🧪",
        totalMinutes = 120, // 2 hours
        course = ZojirushiMenuCourse.SOURDOUGH_STARTER,
        category = RecipeCategory.SOURDOUGH,
        defaultCrust = CrustShade.LIGHT,
        defaultLoafSize = LoafSize.TWO_LB,
        paddleRemovalStageIndex = null,
        tips = "Maintains consistent 82°F wild yeast incubation. Use immediately after 2 hours for Light Sourdough.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "Chamber Temp Equalization", 10, 82, 10, 0),
            BakingStage(BakingStageType.KNEAD_1, "Starter Batter Incorporation", 10, 82, 0, 45),
            BakingStage(BakingStageType.RISE_1, "Controlled 82°F Fermentation Hour 1", 50, 82, 20, 0),
            BakingStage(BakingStageType.RISE_2, "Controlled 82°F Fermentation Hour 2", 50, 82, 20, 0)
        )
    )

    // 10. Homemade Custom Memory Cycle
    val HOMEMADE_CUSTOM = BreadMachineCycle(
        id = "cycle_homemade_custom",
        courseNumber = 14,
        name = "Homemade Programmable Custom Cycle",
        shortName = "Custom Memory",
        description = "User-customized memory slot with personalized durations for each individual baking stage.",
        emoji = "⚙️",
        totalMinutes = 195, // 3 hours 15 minutes
        course = ZojirushiMenuCourse.HOMEMADE_CUSTOM,
        category = RecipeCategory.CUSTOM,
        defaultCrust = CrustShade.ARTISAN_CRISPY,
        defaultLoafSize = LoafSize.TWO_LB,
        paddleRemovalStageIndex = 5,
        dispenserAlertStageIndex = 3,
        tips = "Program and store your exact knead, rise, and bake times in machine memory slots 1, 2, or 3.",
        stages = listOf(
            BakingStage(BakingStageType.PREHEAT, "Preheat Equalize", 15, 80, 10, 0),
            BakingStage(BakingStageType.KNEAD_1, "Knead 1 Mix", 10, 80, 0, 50),
            BakingStage(BakingStageType.REST_AUTOLYSE, "Rest Pause", 15, 80, 0, 0),
            BakingStage(BakingStageType.KNEAD_2, "Knead 2 Gluten Stretch", 20, 82, 0, 110, hasIngredientAlert = true, alertTitle = "Add Ingredients"),
            BakingStage(BakingStageType.RISE_1, "Rise 1 Ferment", 45, 85, 20, 0),
            BakingStage(BakingStageType.PUNCH_DOWN, "Paddle Removal", 3, 85, 0, 30, hasIngredientAlert = true, alertTitle = "Extract Paddles"),
            BakingStage(BakingStageType.RISE_2, "Rise 2 Intermediate", 30, 86, 25, 0),
            BakingStage(BakingStageType.RISE_3, "Rise 3 Final Loaf", 35, 90, 30, 0),
            BakingStage(BakingStageType.BAKE, "Custom Hearth Bake", 45, 385, 90, 0),
            BakingStage(BakingStageType.KEEP_WARM, "Warm Hold", 15, 140, 10, 0)
        )
    )

    val ALL_CYCLES: List<BreadMachineCycle> = listOf(
        WHITE_BREAD,
        WHOLE_WHEAT,
        EUROPEAN_SOURDOUGH,
        MULTIGRAIN,
        GLUTEN_FREE,
        SWEET_BRIOCHE,
        QUICK_RAPID,
        DOUGH_CYCLE,
        SOURDOUGH_STARTER,
        HOMEMADE_CUSTOM
    )
}
