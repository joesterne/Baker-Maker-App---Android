package com.example.data.local

import com.example.data.model.BakingStage
import com.example.data.model.BakingStageType
import com.example.data.model.BreadRecipe
import com.example.data.model.CrustShade
import com.example.data.model.FlourCategory
import com.example.data.model.LoafSize
import com.example.data.model.RecipeIngredient
import com.example.data.model.YeastType
import com.example.data.model.ZojirushiMenuCourse

object PreloadedRecipes {

    val SOURDOUGH_RECIPES: List<BreadRecipe> = listOf(
        ZojirushiManualRecipes.RECIPES[0], // Zojirushi Light Sourdough Bread (Official Manual)
        ZojirushiManualRecipes.RECIPES[1], // Zojirushi Sourdough Starter (Official Manual Course 12)
        BreadRecipe(
            id = "zojirushi_virtuoso_sourdough",
            title = "Zojirushi Virtuoso® Sourdough Loaf",
            subtitle = "Twin-Blade Knead • 2.0 lb European Course",
            description = "Formulated specifically for the Zojirushi BB-PDC20's 2.0 lb horizontal pan with dual kneading blades. Utilizes the Course #3 European cycle with upper lid heating element for an evenly browned, golden blistered crust.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "Unbleached Bread Flour (500g)",
            specialtyFlours = listOf("Stoneground Whole Wheat (50g)"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.ARTISAN_CRISPY,
            hydrationPercent = 72,
            totalTimeMinutes = 260,
            zojirushiRecommendedCourse = ZojirushiMenuCourse.EUROPEAN,
            ingredients = listOf(
                RecipeIngredient("zj1", "Warm Water (80°F)", "360 ml", 72f, BakingStageType.PREHEAT, notes = "Pour in around twin blade shafts"),
                RecipeIngredient("zj2", "Active Ripe Levain (100% hydration)", "150 g", 30f, BakingStageType.PREHEAT, notes = "Spoon gently over water"),
                RecipeIngredient("zj3", "Extra Virgin Olive Oil", "1 tbsp (14g)", 3f, BakingStageType.PREHEAT, notes = "Coats rectangular pan corners"),
                RecipeIngredient("zj4", "High-Protein Bread Flour", "460 g", 92f, BakingStageType.PREHEAT, notes = "Covers liquids completely"),
                RecipeIngredient("zj5", "Stoneground Whole Wheat Flour", "40 g", 8f, BakingStageType.PREHEAT, notes = "Adds rustic aroma"),
                RecipeIngredient("zj6", "Fine Sea Salt", "10 g", 2f, BakingStageType.REST_AUTOLYSE, notes = "Add during autolyse pause"),
                RecipeIngredient("zj7", "Optional: Toasted Sesame & Pumpkin Seeds", "50 g", 10f, BakingStageType.KNEAD_2, isAutoDispenser = true, notes = "Dispenser chime addition")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Zojirushi Preheating Equalization", 20, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Twin Blade Hydration Mix", 10, 82, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Enzyme Rest & Salt Inoculation", 20, 80, 0, 0, hasIngredientAlert = true, alertTitle = "Autolyse Beep: Add 10g Salt"),
                BakingStage(BakingStageType.KNEAD_2, "Twin Blade Gluten Development", 22, 82, 0, 120, hasIngredientAlert = true, alertTitle = "Dispenser Chime: Add Seeds & Nuts"),
                BakingStage(BakingStageType.RISE_1, "Bulk Rise 1", 55, 84, 25, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Twin Paddles Extraction Double-Beep", 3, 84, 0, 30, hasIngredientAlert = true, alertTitle = "BB-PDC20: Remove Both Kneading Blades", alertDescription = "Extract left and right blades from rectangular pan before final proof."),
                BakingStage(BakingStageType.RISE_2, "Intermediate Rise", 35, 86, 30, 0),
                BakingStage(BakingStageType.RISE_3, "Final Proof & Loaf Crown Rise", 40, 90, 35, 0, hasIngredientAlert = true, alertTitle = "Score Loaf & Mist"),
                BakingStage(BakingStageType.BAKE, "Dual-Heater Artisan Bake (Base + Lid)", 55, 395, 95, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Warm Cool Vent", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "The BB-PDC20 horizontal dual blades thoroughly stretch high-protein gluten across the full 2-lb loaf length.",
                "The built-in lid heater guarantees golden top browning without cold spots."
            )
        ),
        BreadRecipe(
            id = "sf_artisan_sourdough",
            title = "San Francisco Artisan Sourdough",
            subtitle = "Wild Levain Ferment • Blistered Ear Crust",
            description = "A classic gold-standard sourdough featuring a 100% hydration wild starter, an extended autolyse stage for maximum enzymatic flavor, and a blistering 390°F final bake.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "Unbleached Bread Flour (12.7% protein)",
            specialtyFlours = listOf("Organic Whole Rye Flour (5%)"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.ONE_HALF_LB,
            defaultCrust = CrustShade.ARTISAN_CRISPY,
            hydrationPercent = 70,
            totalTimeMinutes = 245,
            ingredients = listOf(
                RecipeIngredient("sf1", "Filtered Water (lukewarm 80°F)", "270 ml", 68f, BakingStageType.PREHEAT, notes = "Pour in first at bottom of pan"),
                RecipeIngredient("sf2", "Ripe Sourdough Starter (100% hydration)", "120 g", 30f, BakingStageType.PREHEAT, notes = "Active & bubbly, fed 4-6 hrs prior"),
                RecipeIngredient("sf3", "Unbleached Bread Flour", "380 g", 95f, BakingStageType.PREHEAT, notes = "High protein for elastic gluten matrix"),
                RecipeIngredient("sf4", "Organic Dark Rye Flour", "20 g", 5f, BakingStageType.PREHEAT, notes = "Boosts lactic acid flavor profile"),
                RecipeIngredient("sf5", "Fine Sea Salt", "8 g", 2f, BakingStageType.REST_AUTOLYSE, notes = "Add after initial autolyse rest"),
                RecipeIngredient("sf6", "Optional: Kalamata Olives & Rosemary", "45 g", 11f, BakingStageType.KNEAD_2, isAutoDispenser = true, notes = "Machine dispenser chime trigger"),
                RecipeIngredient("sf7", "Dusting Rice Flour & Semolina", "1 tbsp", 0f, BakingStageType.RISE_3, notes = "Dust loaf top before final bake score")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Autolyse Equalization", 15, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Initial Flour Hydration", 10, 82, 0, 45),
                BakingStage(
                    BakingStageType.REST_AUTOLYSE,
                    "Enzyme Rest & Salt Addition",
                    20,
                    80,
                    0,
                    0,
                    hasIngredientAlert = true,
                    alertTitle = "Autolyse Complete: Add Salt",
                    alertDescription = "Sprinkle the 8g fine sea salt evenly over the hydrated dough now."
                ),
                BakingStage(
                    BakingStageType.KNEAD_2,
                    "Gluten Windowpane Development",
                    22,
                    82,
                    0,
                    120,
                    hasIngredientAlert = true,
                    alertTitle = "Dispenser Chime: Add-ins Ready",
                    alertDescription = "Add olives, walnuts, or herbs now via the dispenser door or pan directly."
                ),
                BakingStage(BakingStageType.RISE_1, "Bulk Lactic Fermentation", 55, 84, 25, 0),
                BakingStage(
                    BakingStageType.PUNCH_DOWN,
                    "Degas & Extract Kneading Paddle",
                    3,
                    84,
                    0,
                    30,
                    hasIngredientAlert = true,
                    alertTitle = "Paddle Extraction Window",
                    alertDescription = "Optional: Lightly flour fingers, remove the metal kneading blade to avoid a bottom hole."
                ),
                BakingStage(BakingStageType.RISE_2, "Tension Rest", 30, 86, 30, 0),
                BakingStage(
                    BakingStageType.RISE_3,
                    "Final Proof & Baker's Score",
                    40,
                    90,
                    35,
                    0,
                    hasIngredientAlert = true,
                    alertTitle = "Baker's Score & Mist Loaf",
                    alertDescription = "Slash top of loaf with a sharp razor blade and mist surface with fine water spray for crisp blisters."
                ),
                BakingStage(BakingStageType.BAKE, "Artisan Crust Caramelization", 50, 390, 95, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Crust Crisp Venting", 20, 140, 15, 0)
            ),
            bakerNotes = listOf(
                "Use high-protein bread flour (minimum 12.5% protein) to support the 70% hydration.",
                "Ensure starter floats in water before mixing.",
                "Removing the paddle during Stage 6 prevents tearing the crumb when unmolding."
            )
        ),
        BreadRecipe(
            id = "country_blonde_sourdough",
            title = "Country Blonde Sourdough Boule",
            subtitle = "75% Hydration • Open Custard Crumb",
            description = "A golden, high-moisture sourdough made with unbleached white wheat and stoneground whole wheat. Produces a light, open crumb and a crisp crackling crust.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "Hard Red Winter Wheat Flour (90%)",
            specialtyFlours = listOf("Stoneground Whole Wheat (10%)"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.ARTISAN_CRISPY,
            hydrationPercent = 75,
            totalTimeMinutes = 260,
            ingredients = listOf(
                RecipeIngredient("cb1", "Warm Filtered Water (82°F)", "330 ml", 75f, BakingStageType.PREHEAT),
                RecipeIngredient("cb2", "Active Levain (100% hydration)", "130 g", 30f, BakingStageType.PREHEAT),
                RecipeIngredient("cb3", "Strong White Bread Flour", "400 g", 90f, BakingStageType.PREHEAT),
                RecipeIngredient("cb4", "Stoneground Whole Wheat", "45 g", 10f, BakingStageType.PREHEAT),
                RecipeIngredient("cb5", "Grey Sea Salt (Sel Gris)", "9 g", 2f, BakingStageType.REST_AUTOLYSE)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Water Tempering", 15, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Initial Hydration Incorporation", 10, 82, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Extended Autolyse & Salt", 25, 80, 0, 0, hasIngredientAlert = true, alertTitle = "Add Grey Sea Salt", alertDescription = "Add 9g grey sea salt over dough."),
                BakingStage(BakingStageType.KNEAD_2, "Intensive Gluten Development", 20, 82, 0, 115),
                BakingStage(BakingStageType.RISE_1, "Bulk Ferment (Lactic Acidity)", 60, 84, 25, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Paddle Extract & Shape", 3, 84, 0, 30, hasIngredientAlert = true, alertTitle = "Extract Paddle"),
                BakingStage(BakingStageType.RISE_2, "Tension Proof", 35, 86, 30, 0),
                BakingStage(BakingStageType.RISE_3, "Final Crown Rise & Razor Score", 42, 90, 35, 0, hasIngredientAlert = true, alertTitle = "Score Loaf Dome", alertDescription = "Score longitudinal cut down center."),
                BakingStage(BakingStageType.BAKE, "Crust Blister Bake", 50, 395, 95, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Warm De-steam", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "75% hydration yields a tender custard crumb.",
                "Leave loaf in unheated machine with lid open for 10 minutes after bake to maximize crust crispness."
            )
        ),
        BreadRecipe(
            id = "tartine_high_hydration",
            title = "Tartine-Style Rustic Sourdough",
            subtitle = "78% Hydration • Deep Caramel Crust",
            description = "Inspired by coastal California artisanal bakeries. High hydration creates an airy, irregular honeycomb crumb structure with a deeply caramelized dark crust.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "Unbleached Bread Flour (85%)",
            specialtyFlours = listOf("Whole Grain Rye (5%)", "Whole Wheat (10%)"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.DARK,
            hydrationPercent = 78,
            totalTimeMinutes = 270,
            ingredients = listOf(
                RecipeIngredient("tt1", "Spring Water (80°F)", "350 ml", 78f, BakingStageType.PREHEAT),
                RecipeIngredient("tt2", "Young Sweet Levain", "140 g", 31f, BakingStageType.PREHEAT),
                RecipeIngredient("tt3", "Bread Flour", "380 g", 85f, BakingStageType.PREHEAT),
                RecipeIngredient("tt4", "Stoneground Whole Wheat", "45 g", 10f, BakingStageType.PREHEAT),
                RecipeIngredient("tt5", "Organic Rye Flour", "25 g", 5f, BakingStageType.PREHEAT),
                RecipeIngredient("tt6", "Fine Sea Salt", "10 g", 2.2f, BakingStageType.REST_AUTOLYSE)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Hydration Soak", 20, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Gentle Fold Mix", 12, 82, 0, 45),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Enzymatic Rest", 25, 80, 0, 0, hasIngredientAlert = true, alertTitle = "Add 10g Salt", alertDescription = "Sprinkle salt and 10ml reserved water."),
                BakingStage(BakingStageType.KNEAD_2, "High Speed Extensibility Knead", 22, 82, 0, 120),
                BakingStage(BakingStageType.RISE_1, "Bulk Aeration Rise", 65, 84, 25, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Gentle De-gas & Paddle Removal", 3, 84, 0, 25, hasIngredientAlert = true, alertTitle = "Extract Kneader"),
                BakingStage(BakingStageType.RISE_2, "Bench Proof", 35, 86, 30, 0),
                BakingStage(BakingStageType.RISE_3, "Final Rise & Dusting", 38, 90, 35, 0, hasIngredientAlert = true, alertTitle = "Dust with Rice Flour"),
                BakingStage(BakingStageType.BAKE, "Deep Roast Caramel Bake", 50, 395, 95, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Moisture Evaporation Hold", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "Use sweet, young levain fed at a 1:4:4 ratio for mild fruity tang rather than harsh acetic acid.",
                "Rice flour dusting on the loaf top creates beautiful contrast with dark blisters."
            )
        ),
        BreadRecipe(
            id = "sourdough_rye_caraway",
            title = "Sourdough Dark Rye & Caraway Batard",
            subtitle = "40% Dark Rye • Wild Levain Ferment",
            description = "Traditional European rye sourdough crafted with natural levain, dark rye flour, and whole toasted caraway seeds. Rich earthy aroma and dense, moist crumb.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "Medium Rye Flour (40%)",
            specialtyFlours = listOf("Strong Bread Flour (60%)", "Whole Caraway (2%)"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.ONE_HALF_LB,
            defaultCrust = CrustShade.DARK,
            hydrationPercent = 68,
            totalTimeMinutes = 235,
            ingredients = listOf(
                RecipeIngredient("src1", "Lukewarm Water (85°F)", "270 ml", 68f, BakingStageType.PREHEAT),
                RecipeIngredient("src2", "Active Sourdough Levain", "120 g", 30f, BakingStageType.PREHEAT),
                RecipeIngredient("src3", "Dark Rye Flour", "160 g", 40f, BakingStageType.PREHEAT),
                RecipeIngredient("src4", "Strong Bread Flour", "240 g", 60f, BakingStageType.PREHEAT),
                RecipeIngredient("src5", "Molasses", "1 tbsp", 4f, BakingStageType.PREHEAT),
                RecipeIngredient("src6", "Fine Sea Salt", "8 g", 2f, BakingStageType.REST_AUTOLYSE),
                RecipeIngredient("src7", "Toasted Caraway Seeds", "2 tsp (6g)", 1.5f, BakingStageType.KNEAD_2, isAutoDispenser = true, notes = "Dispenser chime trigger")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Rye Tempering", 15, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Rye Flour Amalgamation", 10, 82, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Pentosans Swell", 15, 82, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Caraway Incorporation", 18, 82, 0, 80, hasIngredientAlert = true, alertTitle = "Dispenser Chime: Caraway Seeds", alertDescription = "Releasing toasted caraway seeds into dough."),
                BakingStage(BakingStageType.RISE_1, "Rye Acid Ferment", 55, 86, 30, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "De-gas & Remove Paddle", 3, 86, 0, 25, hasIngredientAlert = true, alertTitle = "Extract Paddle"),
                BakingStage(BakingStageType.RISE_2, "Second Rise", 35, 88, 30, 0),
                BakingStage(BakingStageType.RISE_3, "Final Proof", 34, 90, 35, 0),
                BakingStage(BakingStageType.BAKE, "Dark Bavarian Bake", 55, 385, 95, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Rye Hold", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "Rye acidifies naturally under sourdough fermentation, preventing gummy starch gelatinization.",
                "Excellent paired with sharp cheeses and smoked meats."
            )
        ),
        BreadRecipe(
            id = "seeded_artisan_sourdough",
            title = "Seeded Artisan Sourdough",
            subtitle = "Toasted Seeds • Nutty Blistered Dome",
            description = "Natural sourdough inoculated with toasted pumpkin seeds, sesame, and golden flax seeds added during the second knead chime.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "Stoneground Bread Flour",
            specialtyFlours = listOf("Toasted Sesame (4%)", "Pumpkin Seeds (5%)", "Flaxseed (3%)"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.ARTISAN_CRISPY,
            hydrationPercent = 72,
            totalTimeMinutes = 250,
            ingredients = listOf(
                RecipeIngredient("sas1", "Warm Water (80°F)", "310 ml", 72f, BakingStageType.PREHEAT),
                RecipeIngredient("sas2", "Sourdough Starter (100% hydration)", "130 g", 30f, BakingStageType.PREHEAT),
                RecipeIngredient("sas3", "Unbleached Bread Flour", "400 g", 93f, BakingStageType.PREHEAT),
                RecipeIngredient("sas4", "Whole Wheat Flour", "30 g", 7f, BakingStageType.PREHEAT),
                RecipeIngredient("sas5", "Sea Salt", "9 g", 2.1f, BakingStageType.REST_AUTOLYSE),
                RecipeIngredient("sas6", "Toasted Seed Blend (Pumpkin, Flax, Sesame)", "55 g", 13f, BakingStageType.KNEAD_2, isAutoDispenser = true, notes = "Dispenser chime addition")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Equalize", 15, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Hydration Knead", 10, 82, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Autolyse Rest", 20, 80, 0, 0, hasIngredientAlert = true, alertTitle = "Add 9g Sea Salt"),
                BakingStage(BakingStageType.KNEAD_2, "Seed Fold Knead", 22, 82, 0, 110, hasIngredientAlert = true, alertTitle = "Dispenser Beep: 55g Seed Blend", alertDescription = "Automatic dispenser adds toasted seed blend."),
                BakingStage(BakingStageType.RISE_1, "Bulk Rise", 55, 84, 25, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "De-air & Extract Paddle", 3, 84, 0, 30, hasIngredientAlert = true, alertTitle = "Remove Paddle"),
                BakingStage(BakingStageType.RISE_2, "Proofing", 35, 86, 30, 0),
                BakingStage(BakingStageType.RISE_3, "Final Seeded Crown Proof", 40, 90, 35, 0, hasIngredientAlert = true, alertTitle = "Press Extra Seeds on Top"),
                BakingStage(BakingStageType.BAKE, "Crisp Seed Bake", 50, 390, 95, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Warm Cool", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "Pre-toasting seeds enhances nuttiness and prevents them from soaking up water from the dough.",
                "Artisan Crispy crust produces popping toasted sesame flavor."
            )
        ),
        BreadRecipe(
            id = "soft_sourdough_sandwich",
            title = "Soft Sourdough Sandwich Loaf",
            subtitle = "65% Hydration • Honey & Milk Enriched",
            description = "All the gut-healthy benefits of wild sourdough fermentation with the soft, sliceable crumb of a traditional sandwich bread. Perfect for daily toast and sandwiches.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "Unbleached Bread Flour",
            specialtyFlours = listOf("Whole Milk (enriched)", "Clover Honey (3%)"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.ONE_HALF_LB,
            defaultCrust = CrustShade.LIGHT,
            hydrationPercent = 65,
            totalTimeMinutes = 230,
            ingredients = listOf(
                RecipeIngredient("sss1", "Whole Milk (lukewarm 80°F)", "220 ml", 55f, BakingStageType.PREHEAT),
                RecipeIngredient("sss2", "Filtered Water", "40 ml", 10f, BakingStageType.PREHEAT),
                RecipeIngredient("sss3", "Honey", "1.5 tbsp (30g)", 7.5f, BakingStageType.PREHEAT),
                RecipeIngredient("sss4", "Active Sourdough Starter", "110 g", 27f, BakingStageType.PREHEAT),
                RecipeIngredient("sss5", "Bread Flour", "400 g", 100f, BakingStageType.PREHEAT),
                RecipeIngredient("sss6", "Fine Salt", "7 g", 1.7f, BakingStageType.PREHEAT),
                RecipeIngredient("sss7", "Unsalted Butter (softened)", "30 g", 7.5f, BakingStageType.KNEAD_2, notes = "Add at Knead 2 chime")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Milk Tempering", 12, 78, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Flour Mixing", 10, 78, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Dairy Rest", 15, 78, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Butter Emulsification", 20, 80, 0, 110, hasIngredientAlert = true, alertTitle = "Add Softened Butter", alertDescription = "Add 30g softened butter now."),
                BakingStage(BakingStageType.RISE_1, "Enriched Sourdough Ferment", 55, 82, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Paddle Extraction", 3, 82, 0, 30, hasIngredientAlert = true, alertTitle = "Extract Kneader"),
                BakingStage(BakingStageType.RISE_2, "Tension Rest", 35, 84, 25, 0),
                BakingStage(BakingStageType.RISE_3, "Final Sandwich Proof", 40, 88, 30, 0),
                BakingStage(BakingStageType.BAKE, "Golden Sandwich Bake", 45, 350, 80, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Warm Hold", 15, 135, 10, 0)
            ),
            bakerNotes = listOf(
                "Bake on Light crust setting to maintain paper-thin golden crust.",
                "The milk fat softens gluten strands for effortless sandwich slicing."
            )
        )
    )

    // Other specialty flour recipes (Spelt, Roggenbrot, Brioche, GF, Shokupan, Whole Wheat)
    val OTHER_SPECIALTY_RECIPES: List<BreadRecipe> = listOf(
        BreadRecipe(
            id = "rustic_spelt_honey",
            title = "Rustic Spelt & Wildflower Honey",
            subtitle = "100% Ancient Grain Spelt • Nutty Sweet Crumb",
            description = "Ancient spelt grain possesses delicate, soluble gluten that produces a delightfully digestible crumb with toasted sunflower seed additions.",
            category = FlourCategory.SPELT_ANCIENT,
            primaryFlour = "Organic Stoneground Spelt Flour",
            specialtyFlours = listOf("White Spelt (50%)", "Whole Spelt (50%)"),
            yeastType = YeastType.ACTIVE_DRY_YEAST,
            defaultLoafSize = LoafSize.ONE_HALF_LB,
            defaultCrust = CrustShade.MEDIUM,
            hydrationPercent = 64,
            totalTimeMinutes = 195,
            ingredients = listOf(
                RecipeIngredient("sp1", "Lukewarm Buttermilk or Milk", "240 ml", 60f, BakingStageType.PREHEAT),
                RecipeIngredient("sp2", "Raw Wildflower Honey", "2 tbsp (40g)", 10f, BakingStageType.PREHEAT),
                RecipeIngredient("sp3", "Cold Pressed Extra Virgin Olive Oil", "2 tbsp (28g)", 7f, BakingStageType.PREHEAT),
                RecipeIngredient("sp4", "Stoneground Whole Spelt Flour", "200 g", 50f, BakingStageType.PREHEAT),
                RecipeIngredient("sp5", "White Spelt Flour", "200 g", 50f, BakingStageType.PREHEAT),
                RecipeIngredient("sp6", "Active Dry Yeast", "6 g (1.5 tsp)", 1.5f, BakingStageType.PREHEAT),
                RecipeIngredient("sp7", "Sea Salt", "7 g", 1.7f, BakingStageType.PREHEAT),
                RecipeIngredient("sp8", "Toasted Sunflower & Flax Seeds", "40 g", 10f, BakingStageType.KNEAD_2, isAutoDispenser = true)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Liquid Warmth Temper", 10, 80, 15, 0),
                BakingStage(BakingStageType.KNEAD_1, "Gentle Spelt Mixing", 8, 80, 0, 40),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Gluten Hydration Rest", 12, 80, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Gentle Spelt Knead", 14, 80, 0, 65, hasIngredientAlert = true, alertTitle = "Dispenser Beep: Add Seeds", alertDescription = "Automatic dispenser releasing seeds into dough."),
                BakingStage(BakingStageType.RISE_1, "Bulk Ferment", 45, 84, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Gentle Deflate", 2, 84, 0, 30),
                BakingStage(BakingStageType.RISE_2, "Final Spelt Rise", 54, 88, 30, 0),
                BakingStage(BakingStageType.BAKE, "Golden Honey Bake", 45, 360, 85, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Warm Cool Down", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "Spelt gluten is more fragile than modern wheat—avoid over-kneading.",
                "Honey caramelizes rapidly, so Medium crust temperature yields ideal color without scorching."
            )
        ),
        BreadRecipe(
            id = "classic_brioche_feuilletee",
            title = "Golden French Brioche",
            subtitle = "Enriched Butter Crumb • Pearl Sugar Crust",
            description = "Rich, cloud-like brioche loaded with egg yolks and chilled butter cubes incorporated gradually at the Knead 2 chime for supreme feather-light flakiness.",
            category = FlourCategory.BRIOCHE_SWEET,
            primaryFlour = "French T45 or Strong Bread Flour",
            specialtyFlours = listOf("Pastry Flour (15%)"),
            yeastType = YeastType.INSTANT_YEAST,
            defaultLoafSize = LoafSize.ONE_HALF_LB,
            defaultCrust = CrustShade.LIGHT,
            hydrationPercent = 52,
            totalTimeMinutes = 210,
            ingredients = listOf(
                RecipeIngredient("br1", "Whole Milk (cold 50°F)", "110 ml", 28f, BakingStageType.PREHEAT),
                RecipeIngredient("br2", "Large Eggs (lightly beaten)", "3 whole (150g)", 38f, BakingStageType.PREHEAT),
                RecipeIngredient("br3", "Caster Sugar", "45 g", 11f, BakingStageType.PREHEAT),
                RecipeIngredient("br4", "Fine Bread Flour", "360 g", 90f, BakingStageType.PREHEAT),
                RecipeIngredient("br5", "Instant Dry Yeast", "6 g", 1.5f, BakingStageType.PREHEAT),
                RecipeIngredient("br6", "Fine Sea Salt", "6 g", 1.5f, BakingStageType.PREHEAT),
                RecipeIngredient("br7", "Chilled European Butter (diced 1/2-inch)", "120 g (82% fat)", 30f, BakingStageType.KNEAD_2, notes = "Cold cubes added at Knead 2 chime!"),
                RecipeIngredient("br8", "Pearl Sugar & Egg Wash", "2 tbsp", 0f, BakingStageType.RISE_3)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Egg & Milk Temper", 10, 75, 0, 0),
                BakingStage(BakingStageType.KNEAD_1, "Initial Dough Cohesion", 12, 76, 0, 70),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Gluten Chill Rest", 10, 76, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Cold Butter Emulsification", 25, 78, 0, 120, hasIngredientAlert = true, alertTitle = "CRITICAL: Add Chilled Butter Cubes", alertDescription = "Drop in cold diced butter cubes 2-3 at a time over 5 minutes."),
                BakingStage(BakingStageType.RISE_1, "Slow Butter Proof", 55, 80, 15, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "De-air & Shape", 3, 80, 0, 25),
                BakingStage(BakingStageType.RISE_2, "Second Rise", 35, 82, 20, 0),
                BakingStage(BakingStageType.RISE_3, "Final Proof & Egg Wash", 25, 82, 20, 0, hasIngredientAlert = true, alertTitle = "Egg Wash & Pearl Sugar Alert", alertDescription = "Brush dome with egg wash and sprinkle Belgian pearl sugar."),
                BakingStage(BakingStageType.BAKE, "Gentle Golden Bake", 40, 340, 75, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Butter Crust Set", 10, 130, 10, 0)
            ),
            bakerNotes = listOf(
                "Never add butter with liquid at the beginning—it coats gluten strands.",
                "Bake on Light crust setting to avoid burning the high sugar and butter content."
            )
        ),
        BreadRecipe(
            id = "japanese_shokupan",
            title = "Japanese Hokkaido Milk Bread",
            subtitle = "Tangzhong Roux Method • Cloud-Soft Pull Apart",
            description = "The pinnacle of tender sandwich bread. Uses the Tangzhong cooked flour-water paste method to trap moisture for days of unmatched cloud-like fluffiness.",
            category = FlourCategory.SPECIALTY,
            primaryFlour = "Japanese or High-Gluten Bread Flour",
            specialtyFlours = listOf("Dry Whole Milk Powder (8%)"),
            yeastType = YeastType.INSTANT_YEAST,
            defaultLoafSize = LoafSize.ONE_HALF_LB,
            defaultCrust = CrustShade.LIGHT,
            hydrationPercent = 65,
            totalTimeMinutes = 205,
            ingredients = listOf(
                RecipeIngredient("jp1", "Tangzhong Roux (20g flour + 100ml water paste)", "110 g", 27f, BakingStageType.PREHEAT),
                RecipeIngredient("jp2", "Whole Milk (lukewarm 80°F)", "130 ml", 32f, BakingStageType.PREHEAT),
                RecipeIngredient("jp3", "Heavy Cream or Sweetened Condensed Milk", "30 ml", 7.5f, BakingStageType.PREHEAT),
                RecipeIngredient("jp4", "Caster Sugar", "35 g", 8.7f, BakingStageType.PREHEAT),
                RecipeIngredient("jp5", "Bread Flour (12.5%+)", "400 g", 100f, BakingStageType.PREHEAT),
                RecipeIngredient("jp6", "Whole Milk Powder (Baker's)", "15 g", 3.7f, BakingStageType.PREHEAT),
                RecipeIngredient("jp7", "Instant Yeast", "6 g", 1.5f, BakingStageType.PREHEAT),
                RecipeIngredient("jp8", "Fine Salt", "6 g", 1.5f, BakingStageType.PREHEAT),
                RecipeIngredient("jp9", "Unsalted Butter (softened)", "35 g", 8.7f, BakingStageType.KNEAD_2)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Tangzhong Equalize", 10, 78, 0, 0),
                BakingStage(BakingStageType.KNEAD_1, "Milk Dough Mix", 10, 78, 0, 60),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Dairy Hydration Rest", 10, 78, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Soft Butter Knead", 22, 80, 0, 120, hasIngredientAlert = true, alertTitle = "Add Softened Butter", alertDescription = "Add 35g softened butter to achieve translucent windowpane stretch."),
                BakingStage(BakingStageType.RISE_1, "Sweet Bulk Ferment", 50, 84, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Degas & Paddle Removal", 3, 84, 0, 30),
                BakingStage(BakingStageType.RISE_2, "Rest & Crown Formation", 35, 86, 25, 0),
                BakingStage(BakingStageType.RISE_3, "Final Shokupan Rise", 35, 90, 30, 0),
                BakingStage(BakingStageType.BAKE, "Featherweight Bake", 38, 345, 80, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Crumb Steam Hold", 10, 135, 10, 0)
            ),
            bakerNotes = listOf(
                "Tangzhong pre-gelatinizes starch, trapping 20% more moisture.",
                "Select Light crust for melt-in-your-mouth paper-thin crust."
            )
        )
    )

    // Combined list: sourdough + specialty + official Zojirushi BB-PDC20 manual recipes
    val RECIPES: List<BreadRecipe> = SOURDOUGH_RECIPES + OTHER_SPECIALTY_RECIPES + ZojirushiManualRecipes.RECIPES.drop(2)
}
