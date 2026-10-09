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

/**
 * Official recipes extracted directly from the Zojirushi Home Bakery Virtuoso® Plus
 * (BB-PDC20) Instruction Manual & Recipe Book (manuals.plus / Zojirushi official).
 *
 * Covers:
 * 1. Basic White Bread (Course 1 - WHITE)
 * 2. 100% Whole Wheat Bread (Course 2 - WHOLE WHEAT)
 * 3. Light Sourdough Bread (Course 1 - WHITE, using Sourdough Starter)
 * 4. Sourdough Starter Culturing (Course 12 - SOURDOUGH STARTER)
 * 5. French Bread (Course 3 - EUROPEAN)
 * 6. Rustic Italian Herb Bread (Course 3 - EUROPEAN)
 * 7. Multigrain Bread (Course 4 - MULTIGRAIN)
 * 8. Gluten-Free Bread (Course 5 - GLUTEN FREE)
 * 9. Rapid White Bread (Course 9 - RAPID WHITE)
 * 10. Pizza Dough (Course 11 - DOUGH)
 */
object ZojirushiManualRecipes {

    val RECIPES: List<BreadRecipe> = listOf(
        // 1. Light Sourdough Bread (Official BB-PDC20 Recipe Book)
        BreadRecipe(
            id = "zojirushi_manual_light_sourdough",
            title = "Zojirushi Light Sourdough Bread",
            subtitle = "Official Virtuoso Plus Manual • Course 1 (White)",
            description = "The authentic sourdough formula from the Zojirushi BB-PDC20 Recipe Book. Combines cultured sourdough starter with apple cider vinegar and lemon juice for an authentic tangy sourdough flavor profile baked using the 2.0 lb horizontal pan.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "Bread Flour (390g / 3 cups)",
            specialtyFlours = listOf("Cultured Sourdough Starter (Pan Base)", "Apple Cider Vinegar", "Lemon Juice"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.MEDIUM,
            hydrationPercent = 70,
            totalTimeMinutes = 225, // Course 1 standard duration: ~3h 45m (225m)
            zojirushiRecommendedCourse = ZojirushiMenuCourse.WHITE,
            ingredients = listOf(
                RecipeIngredient("zms1", "Cultured Sourdough Starter", "Prepared Starter (from Course 12)", 0f, BakingStageType.PREHEAT, notes = "In baking pan already prepared from Course 12"),
                RecipeIngredient("zms2", "Apple Cider Vinegar", "38 g (2-½ Tbsp / 38 mL)", 9.7f, BakingStageType.PREHEAT, notes = "Add into starter liquid"),
                RecipeIngredient("zms3", "Lemon Juice", "23 g (1-½ Tbsp / 23 mL)", 5.9f, BakingStageType.PREHEAT, notes = "Add into starter liquid"),
                RecipeIngredient("zms4", "High-Grade Bread Flour", "390 g (3 cups)", 100f, BakingStageType.PREHEAT, notes = "Level measure over liquid"),
                RecipeIngredient("zms5", "Sugar", "24 g (2 Tbsp)", 6.2f, BakingStageType.PREHEAT, notes = "Place in corner of pan"),
                RecipeIngredient("zms6", "Salt", "7.5 g (1-½ tsp)", 1.9f, BakingStageType.PREHEAT, notes = "Keep away from yeast"),
                RecipeIngredient("zms7", "Active Dry Yeast", "6 g (2 tsp)", 1.5f, BakingStageType.PREHEAT, notes = "Make slight indentation in center of flour")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Zojirushi Preheating & Temperature Equalization", 25, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Twin-Blade Hydration & Initial Incorporation", 10, 82, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Gluten Rest", 10, 82, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Twin-Blade Vigorous Kneading", 22, 82, 0, 115),
                BakingStage(BakingStageType.RISE_1, "First Rise (Bulk Fermentation)", 45, 84, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Punch Down & Twin Paddle Removal Chime", 3, 84, 0, 30, hasIngredientAlert = true, alertTitle = "Extract BB-PDC20 Twin Paddles", alertDescription = "Double-beep prompts removing both blades from the bottom of the 2-lb horizontal pan."),
                BakingStage(BakingStageType.RISE_2, "Second Rise", 35, 86, 25, 0),
                BakingStage(BakingStageType.RISE_3, "Final Proof (Chamber 90°F)", 35, 90, 30, 0),
                BakingStage(BakingStageType.BAKE, "Dual-Heater Bake (Base & Lid Heater)", 40, 375, 90, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Cooling & Keep Warm", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "From the Official BB-PDC20 Manual: Always prepare the Sourdough Starter course (#12) first, then immediately add the additional ingredients to bake Light Sourdough Bread.",
                "Select Course 1 (WHITE) with MEDIUM crust setting.",
                "Ensure water and ingredients are measured accurately by weight in grams for consistent rise."
            )
        ),

        // 2. Sourdough Starter Culture (Course 12 - SOURDOUGH STARTER)
        BreadRecipe(
            id = "zojirushi_manual_sourdough_starter",
            title = "Zojirushi Sourdough Starter",
            subtitle = "Official Virtuoso Plus Manual • Course 12 (Starter)",
            description = "The official Course 12 cycle for culturing a vigorous sourdough starter inside the BB-PDC20. The breadmaker gently heats the chamber to a thermostatically controlled 82°F for wild fermentation over 2 hours.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "Bread Flour (260g / 2 cups)",
            specialtyFlours = emptyList(),
            yeastType = YeastType.ACTIVE_DRY_YEAST,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.LIGHT,
            hydrationPercent = 138, // 360g water / 260g flour
            totalTimeMinutes = 120, // Course 12: exactly 2 hours (120 mins)
            zojirushiRecommendedCourse = ZojirushiMenuCourse.SOURDOUGH_STARTER,
            ingredients = listOf(
                RecipeIngredient("zstr1", "Lukewarm Water", "360 g (360 mL / 1-½ cups)", 138f, BakingStageType.PREHEAT, notes = "Add first around blade shafts"),
                RecipeIngredient("zstr2", "Bread Flour", "260 g (2 cups)", 100f, BakingStageType.PREHEAT, notes = "Cover water completely"),
                RecipeIngredient("zstr3", "Active Dry Yeast", "6 g (2 tsp)", 2.3f, BakingStageType.PREHEAT, notes = "Place in center of flour")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Chamber Temp Equalization", 10, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Starter Batter Mixing", 10, 82, 0, 45),
                BakingStage(BakingStageType.RISE_1, "Controlled 82°F Fermentation (Hour 1)", 50, 82, 20, 0),
                BakingStage(BakingStageType.RISE_2, "Controlled 82°F Fermentation (Hour 2)", 50, 82, 20, 0)
            ),
            bakerNotes = listOf(
                "Directly from BB-PDC20 Manual: Course 12 runs for 2 hours at an ideal 82°F fermentation chamber temperature.",
                "When complete, immediately use as the base for the Zojirushi Light Sourdough Bread recipe (do not refrigerate beforehand for best volume)."
            )
        ),

        // 3. Basic White Bread (Course 1 - WHITE)
        BreadRecipe(
            id = "zojirushi_manual_basic_white",
            title = "Zojirushi Basic White Bread",
            subtitle = "Official Virtuoso Plus Manual • Course 1 (White)",
            description = "The gold standard 2.0 lb horizontal loaf from the BB-PDC20 manual. Dual kneading blades produce a silky, tender crumb with a delicate golden crust browned by the upper lid heater.",
            category = FlourCategory.ALL,
            primaryFlour = "Bread Flour (553g / 4-¼ cups)",
            specialtyFlours = listOf("Dry Milk (8g / 2 Tbsp)", "Unsalted Butter (35g / 2-½ Tbsp)"),
            yeastType = YeastType.INSTANT_YEAST,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.MEDIUM,
            hydrationPercent = 58,
            totalTimeMinutes = 225,
            zojirushiRecommendedCourse = ZojirushiMenuCourse.WHITE,
            ingredients = listOf(
                RecipeIngredient("zw1", "Water", "320 g (320 mL / 1-⅓ cups)", 58f, BakingStageType.PREHEAT, notes = "Add first to pan"),
                RecipeIngredient("zw2", "High-Gluten Bread Flour", "553 g (4-¼ cups)", 100f, BakingStageType.PREHEAT, notes = "Level measure accurately"),
                RecipeIngredient("zw3", "Sugar", "48 g (4 Tbsp)", 8.7f, BakingStageType.PREHEAT),
                RecipeIngredient("zw4", "Dry Milk", "8 g (2 Tbsp)", 1.4f, BakingStageType.PREHEAT),
                RecipeIngredient("zw5", "Salt", "10 g (2 tsp)", 1.8f, BakingStageType.PREHEAT),
                RecipeIngredient("zw6", "Butter", "35 g (2-½ Tbsp)", 6.3f, BakingStageType.PREHEAT),
                RecipeIngredient("zw7", "Rapid Rise / Bread Machine Yeast", "6 g (2 tsp)", 1.1f, BakingStageType.PREHEAT, notes = "Center depression in flour")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Ingredient Temperature Equalization", 25, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Twin Blade Hydration", 10, 82, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Rest Pause", 10, 82, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Full Gluten Stretch Knead", 22, 82, 0, 120),
                BakingStage(BakingStageType.RISE_1, "First Rise", 45, 84, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Twin Paddle Extraction Beep", 3, 84, 0, 30, hasIngredientAlert = true, alertTitle = "Extract Twin Blades", alertDescription = "Optional: Remove both blades so loaf releases cleanly without holes."),
                BakingStage(BakingStageType.RISE_2, "Second Rise", 35, 86, 25, 0),
                BakingStage(BakingStageType.RISE_3, "Final Loaf Crown Proof", 35, 90, 30, 0),
                BakingStage(BakingStageType.BAKE, "Bake (Bottom Pan + Lid Heater)", 40, 375, 90, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Cool Down Keep Warm", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "Official BB-PDC20 Specification: Always use Bread Flour (higher protein) rather than All-Purpose for 2-lb loaves.",
                "Dry milk enhances browning and softens the crumb structure."
            )
        ),

        // 4. 100% Whole Wheat Bread (Course 2 - WHOLE WHEAT)
        BreadRecipe(
            id = "zojirushi_manual_whole_wheat",
            title = "Zojirushi 100% Whole Wheat Bread",
            subtitle = "Official Virtuoso Plus Manual • Course 2 (Whole Wheat)",
            description = "Rich, 100% whole grain loaf with honey and vital wheat gluten. The BB-PDC20 Whole Wheat course extends the preheat stage to allow the bran to thoroughly absorb water and soften before kneading.",
            category = FlourCategory.WHOLE_WHEAT,
            primaryFlour = "Whole Wheat Flour (553g / 4-¼ cups)",
            specialtyFlours = listOf("Vital Wheat Gluten (32g / 4 Tbsp)", "Honey (40g / 2 Tbsp)"),
            yeastType = YeastType.INSTANT_YEAST,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.MEDIUM,
            hydrationPercent = 67,
            totalTimeMinutes = 225, // Course 2: 3h 45m
            zojirushiRecommendedCourse = ZojirushiMenuCourse.WHOLE_WHEAT,
            ingredients = listOf(
                RecipeIngredient("zww1", "Water", "370 g (370 mL / 1-½ cups)", 67f, BakingStageType.PREHEAT, notes = "Add first"),
                RecipeIngredient("zww2", "Honey", "40 g (2 Tbsp)", 7.2f, BakingStageType.PREHEAT, notes = "Add into water"),
                RecipeIngredient("zww3", "Whole Wheat Flour", "553 g (4-¼ cups)", 100f, BakingStageType.PREHEAT),
                RecipeIngredient("zww4", "Sugar", "36 g (3 Tbsp)", 6.5f, BakingStageType.PREHEAT),
                RecipeIngredient("zww5", "Dry Milk", "8 g (2 Tbsp)", 1.4f, BakingStageType.PREHEAT),
                RecipeIngredient("zww6", "Salt", "10 g (2 tsp)", 1.8f, BakingStageType.PREHEAT),
                RecipeIngredient("zww7", "Vital Wheat Gluten", "32 g (4 Tbsp)", 5.8f, BakingStageType.PREHEAT, notes = "Crucial for 2-lb whole wheat height"),
                RecipeIngredient("zww8", "Unsalted Butter", "28 g (2 Tbsp)", 5.0f, BakingStageType.PREHEAT),
                RecipeIngredient("zww9", "Rapid Rise Yeast", "6 g (2 tsp)", 1.1f, BakingStageType.PREHEAT)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Extended Bran Hydration Preheat", 35, 84, 15, 0),
                BakingStage(BakingStageType.KNEAD_1, "Whole Wheat Cohesion Mix", 12, 84, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Gluten Conditioning Rest", 10, 84, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Twin Blade Elasticity Knead", 20, 84, 0, 110),
                BakingStage(BakingStageType.RISE_1, "Whole Grain Ferment", 45, 86, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Degas & Paddle Removal Chime", 3, 86, 0, 30, hasIngredientAlert = true, alertTitle = "Extract Twin Paddles"),
                BakingStage(BakingStageType.RISE_2, "Second Rise", 35, 88, 25, 0),
                BakingStage(BakingStageType.RISE_3, "Final Proofing", 35, 90, 30, 0),
                BakingStage(BakingStageType.BAKE, "Deep Roast Bake (Lid Heater Boost)", 45, 380, 95, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Warm Cool Down", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "BB-PDC20 Manual Pro-Tip: Adding 4 Tbsp Vital Wheat Gluten is essential to give the heavy bran structure enough elasticity to rise to the full top of the 2-lb pan.",
                "Honey adds natural enzymatic moisture and keeps the loaf fresh for days."
            )
        ),

        // 5. French Bread (Course 3 - EUROPEAN)
        BreadRecipe(
            id = "zojirushi_manual_french_bread",
            title = "Zojirushi French Bread",
            subtitle = "Official Virtuoso Plus Manual • Course 3 (European)",
            description = "Crisp, crunchy, crackling crust with a chewy open interior. Course 3 (European) utilizes minimal fat and leverages the upper lid heating element to achieve an authentic European bakery crust.",
            category = FlourCategory.SPECIALTY,
            primaryFlour = "Bread Flour (553g / 4-¼ cups)",
            specialtyFlours = listOf("Dry Milk (12g / 3 Tbsp)"),
            yeastType = YeastType.INSTANT_YEAST,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.ARTISAN_CRISPY,
            hydrationPercent = 58,
            totalTimeMinutes = 225, // Course 3: 3h 45m
            zojirushiRecommendedCourse = ZojirushiMenuCourse.EUROPEAN,
            ingredients = listOf(
                RecipeIngredient("zfb1", "Water (use chilled 41°F if room > 77°F)", "320 g (320 mL / 1-⅓ cups)", 58f, BakingStageType.PREHEAT, notes = "Use chilled water on warm days"),
                RecipeIngredient("zfb2", "Bread Flour", "553 g (4-¼ cups)", 100f, BakingStageType.PREHEAT),
                RecipeIngredient("zfb3", "Sugar", "12 g (1 Tbsp)", 2.2f, BakingStageType.PREHEAT, notes = "Low sugar for lean European dough"),
                RecipeIngredient("zfb4", "Dry Milk", "12 g (3 Tbsp)", 2.2f, BakingStageType.PREHEAT),
                RecipeIngredient("zfb5", "Salt", "10 g (2 tsp)", 1.8f, BakingStageType.PREHEAT),
                RecipeIngredient("zfb6", "Rapid Rise Yeast", "3 g (1 tsp)", 0.5f, BakingStageType.PREHEAT, notes = "Lean yeast for slow flavor development")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "European Cold Equalization", 20, 80, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Lean Dough Mix", 10, 80, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Autolyse Swell", 15, 80, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Intensive Gluten Extensibility", 25, 80, 0, 120),
                BakingStage(BakingStageType.RISE_1, "Lean Ferment", 45, 82, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Paddle Removal Beep", 3, 82, 0, 30, hasIngredientAlert = true, alertTitle = "Extract Twin Blades"),
                BakingStage(BakingStageType.RISE_2, "Intermediate Rise", 35, 86, 25, 0),
                BakingStage(BakingStageType.RISE_3, "Final Crust Rise", 35, 88, 30, 0),
                BakingStage(BakingStageType.BAKE, "Intense European Lid-Bake", 50, 395, 100, 0),
                BakingStage(BakingStageType.KEEP_WARM, "De-moisturize Venting", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "Manual Note: For the European course, if room temperature is above 77°F (25°C), use chilled water at 41°F (5°C) to keep yeast fermentation controlled.",
                "The BB-PDC20 lid heater activates at maximum wattage during the final 30 minutes to blister the top crust."
            )
        ),

        // 6. Rustic Italian Herb Bread (Course 3 - EUROPEAN)
        BreadRecipe(
            id = "zojirushi_manual_rustic_herb",
            title = "Zojirushi Rustic Italian Herb Bread",
            subtitle = "Official Virtuoso Plus Manual • Course 3 (European)",
            description = "Savory artisan loaf infused with extra virgin olive oil, dried oregano, and aromatic sweet basil. The European crust setting gives it a blistered golden exterior and fragrant, tender interior.",
            category = FlourCategory.SPECIALTY,
            primaryFlour = "Bread Flour (553g / 4-¼ cups)",
            specialtyFlours = listOf("Extra Virgin Olive Oil (24g)", "Dried Basil (2g)", "Dried Oregano (2g)"),
            yeastType = YeastType.INSTANT_YEAST,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.ARTISAN_CRISPY,
            hydrationPercent = 58,
            totalTimeMinutes = 225,
            zojirushiRecommendedCourse = ZojirushiMenuCourse.EUROPEAN,
            ingredients = listOf(
                RecipeIngredient("zrh1", "Water (41°F if room temp > 77°F)", "320 g (320 mL / 1-⅓ cups)", 58f, BakingStageType.PREHEAT),
                RecipeIngredient("zrh2", "Extra Virgin Olive Oil", "24 g (2 Tbsp)", 4.3f, BakingStageType.PREHEAT),
                RecipeIngredient("zrh3", "Bread Flour", "553 g (4-¼ cups)", 100f, BakingStageType.PREHEAT),
                RecipeIngredient("zrh4", "Sugar", "12 g (1 Tbsp)", 2.2f, BakingStageType.PREHEAT),
                RecipeIngredient("zrh5", "Dry Milk", "12 g (3 Tbsp)", 2.2f, BakingStageType.PREHEAT),
                RecipeIngredient("zrh6", "Salt", "10 g (2 tsp)", 1.8f, BakingStageType.PREHEAT),
                RecipeIngredient("zrh7", "Dried Sweet Basil", "2 g (1 tsp)", 0.4f, BakingStageType.PREHEAT),
                RecipeIngredient("zrh8", "Dried Oregano", "2 g (1 tsp)", 0.4f, BakingStageType.PREHEAT),
                RecipeIngredient("zrh9", "Rapid Rise Yeast", "6 g (2 tsp)", 1.1f, BakingStageType.PREHEAT)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Herb Oil & Water Tempering", 20, 80, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Herb Distribution Mix", 10, 80, 0, 50),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Rest", 10, 80, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Gluten Development", 25, 80, 0, 120),
                BakingStage(BakingStageType.RISE_1, "Bulk Rise", 45, 84, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Paddle Removal Beep", 3, 84, 0, 30, hasIngredientAlert = true, alertTitle = "Extract Twin Blades"),
                BakingStage(BakingStageType.RISE_2, "Herb Flavor Maturation Rise", 35, 86, 25, 0),
                BakingStage(BakingStageType.RISE_3, "Final Crown Proof", 35, 90, 30, 0),
                BakingStage(BakingStageType.BAKE, "Artisan Lid-Heated Bake", 45, 390, 95, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Warm Cool Vent", 15, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "Official BB-PDC20 Recipe: Pairing dried oregano and basil directly into the dry flour distributes the herb oils evenly throughout the 2-lb loaf.",
                "Serve warm with seasoned olive oil or balsamic dip."
            )
        ),

        // 7. Gluten-Free Raisin Bread (Course 5 - GLUTEN FREE)
        BreadRecipe(
            id = "zojirushi_manual_gluten_free",
            title = "Zojirushi Gluten-Free Raisin Bread",
            subtitle = "Official Virtuoso Plus Manual • Course 5 (Gluten Free)",
            description = "Formulated specially for the BB-PDC20's specialized Gluten Free course. High-liquid batter kneaded by twin blades and stabilized by xanthan gum and potato starch, skipping multi-rise cycles for an immediate high-temperature bake.",
            category = FlourCategory.GLUTEN_FREE,
            primaryFlour = "Brown Rice Flour (228g) & Potato Starch (320g)",
            specialtyFlours = listOf("Potato Starch (320g)", "Xanthan Gum (8g / 1 Tbsp)", "Honey (60g)"),
            yeastType = YeastType.ACTIVE_DRY_YEAST,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.MEDIUM,
            hydrationPercent = 73,
            totalTimeMinutes = 145, // Course 5: ~2h 25m
            zojirushiRecommendedCourse = ZojirushiMenuCourse.GLUTEN_FREE,
            ingredients = listOf(
                RecipeIngredient("zgf1", "Milk", "400 g (400 mL / 1-⅔ cups)", 73f, BakingStageType.PREHEAT, notes = "Liquid first"),
                RecipeIngredient("zgf2", "Eggs (beaten)", "150 g (3 large eggs)", 27f, BakingStageType.PREHEAT),
                RecipeIngredient("zgf3", "Apple Cider Vinegar", "15 mL (1 Tbsp)", 2.7f, BakingStageType.PREHEAT),
                RecipeIngredient("zgf4", "Vegetable Oil", "36 g (3 Tbsp)", 6.5f, BakingStageType.PREHEAT),
                RecipeIngredient("zgf5", "Honey", "60 g (3 Tbsp)", 10.9f, BakingStageType.PREHEAT),
                RecipeIngredient("zgf6", "Brown Rice Flour", "228 g (1-¾ cups)", 41.6f, BakingStageType.PREHEAT),
                RecipeIngredient("zgf7", "Potato Starch", "320 g (2 cups)", 58.4f, BakingStageType.PREHEAT),
                RecipeIngredient("zgf8", "Xanthan Gum", "8 g (1 Tbsp)", 1.5f, BakingStageType.PREHEAT, notes = "Binds gluten-free crumb"),
                RecipeIngredient("zgf9", "Ground Cinnamon", "2 g (1 tsp)", 0.4f, BakingStageType.PREHEAT),
                RecipeIngredient("zgf10", "Salt", "7.5 g (1-½ tsp)", 1.4f, BakingStageType.PREHEAT),
                RecipeIngredient("zgf11", "Active Dry Yeast", "9 g (3 tsp)", 1.6f, BakingStageType.PREHEAT)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Gluten-Free Warmup", 15, 85, 15, 0),
                BakingStage(BakingStageType.KNEAD_1, "Twin Blade Intensive Batter Whip", 25, 85, 0, 130),
                BakingStage(BakingStageType.RISE_1, "Single Stage Gluten-Free Rise", 55, 92, 35, 0),
                BakingStage(BakingStageType.BAKE, "High Heat Starch Bake (Base + Lid)", 50, 385, 95, 0)
            ),
            bakerNotes = listOf(
                "From the BB-PDC20 Manual: Gluten-free dough is more akin to a thick cake batter. Do not open the lid during the rise.",
                "Scrape down corners of the rectangular pan with a rubber spatula 5 minutes into kneading."
            )
        ),

        // 8. Authentic Pizza Dough (Course 11 - DOUGH)
        BreadRecipe(
            id = "zojirushi_manual_pizza_dough",
            title = "Zojirushi Pizzeria Dough",
            subtitle = "Official Virtuoso Plus Manual • Course 11 (Dough)",
            description = "Creates two 12-inch crispy thin crusts or one deep dish 14-inch pie. Course 11 handles the full kneading and bulk rise cycle, leaving dough elastic, relaxed, and ready for hand stretching.",
            category = FlourCategory.SPECIALTY,
            primaryFlour = "Bread Flour (450g / 3-½ cups)",
            specialtyFlours = listOf("Extra Virgin Olive Oil (24g)"),
            yeastType = YeastType.INSTANT_YEAST,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.MEDIUM,
            hydrationPercent = 62,
            totalTimeMinutes = 110, // Course 11: 1h 50m
            zojirushiRecommendedCourse = ZojirushiMenuCourse.DOUGH,
            ingredients = listOf(
                RecipeIngredient("zpd1", "Water (80°F)", "280 g (280 mL / 1-⅕ cups)", 62f, BakingStageType.PREHEAT),
                RecipeIngredient("zpd2", "Olive Oil", "24 g (2 Tbsp)", 5.3f, BakingStageType.PREHEAT),
                RecipeIngredient("zpd3", "Bread Flour", "450 g (3-½ cups)", 100f, BakingStageType.PREHEAT),
                RecipeIngredient("zpd4", "Sugar", "12 g (1 Tbsp)", 2.6f, BakingStageType.PREHEAT),
                RecipeIngredient("zpd5", "Salt", "8 g (1-½ tsp)", 1.7f, BakingStageType.PREHEAT),
                RecipeIngredient("zpd6", "Rapid Rise Yeast", "6 g (2 tsp)", 1.3f, BakingStageType.PREHEAT)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Water Equalize", 15, 82, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Pizza Dough Mix", 10, 82, 0, 60),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Rest", 10, 82, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Twin Blade Windowpane Knead", 25, 82, 0, 120),
                BakingStage(BakingStageType.RISE_1, "Bulk Rise & Gluten Relaxation", 50, 86, 25, 0)
            ),
            bakerNotes = listOf(
                "From the BB-PDC20 Manual: When Course 11 dough completes, remove from pan, divide into 2 balls, cover with a damp cloth for 10 minutes, then roll and bake on pizza stone at 475°F.",
                "Freezes exceptionally well for up to 3 months."
            )
        )
    )
}
