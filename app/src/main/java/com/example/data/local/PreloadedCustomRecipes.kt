package com.example.data.local

import com.example.data.model.BakingStage
import com.example.data.model.BakingStageType
import com.example.data.model.CrustShade
import com.example.data.model.FlourCategory
import com.example.data.model.LoafSize
import com.example.data.model.RecipeIngredient
import com.example.data.model.YeastType

object PreloadedCustomRecipes {

    val INITIAL_CUSTOM_RECIPES: List<CustomRecipeEntity> = listOf(
        CustomRecipeEntity(
            id = "custom_rosemary_olive_artisan",
            title = "Rosemary & Kalamata Olive Artisan Loaf",
            subtitle = "Custom Recipe • Mediterranean Herbed Sourdough",
            description = "Crafted in the custom recipe builder. High-hydration artisan sourdough studded with pitted Kalamata olives and fresh garden rosemary infused during the secondary knead phase.",
            category = FlourCategory.SOURDOUGH,
            primaryFlour = "King Arthur Unbleached Bread Flour (450g)",
            specialtyFlours = listOf("Durum Semolina Rimacinata (50g)"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.ARTISAN_CRISPY,
            hydrationPercent = 74,
            totalTimeMinutes = 245,
            ingredients = listOf(
                RecipeIngredient("cro_1", "Filtered Spring Water (80°F)", "370 ml", 74f, BakingStageType.PREHEAT, notes = "Base hydration"),
                RecipeIngredient("cro_2", "Active 100% Sourdough Levain", "120 g", 24f, BakingStageType.PREHEAT, notes = "Ripe bubbly starter"),
                RecipeIngredient("cro_3", "Extra Virgin Olive Oil", "2 tbsp (28g)", 5.6f, BakingStageType.PREHEAT, notes = "Crumb tenderness"),
                RecipeIngredient("cro_4", "Unbleached Artisan Bread Flour", "450 g", 90f, BakingStageType.PREHEAT, notes = "High gluten structure"),
                RecipeIngredient("cro_5", "Semolina Rimacinata Flour", "50 g", 10f, BakingStageType.PREHEAT, notes = "Golden crust tone"),
                RecipeIngredient("cro_6", "Fine Sea Salt", "10 g", 2f, BakingStageType.REST_AUTOLYSE, notes = "Added after autolyse"),
                RecipeIngredient("cro_7", "Pitted Kalamata Olives (drained)", "75 g", 15f, BakingStageType.KNEAD_2, isAutoDispenser = true, notes = "Auto-dispenser chime"),
                RecipeIngredient("cro_8", "Fresh Chopped Rosemary", "2 tbsp (8g)", 1.6f, BakingStageType.KNEAD_2, isAutoDispenser = true, notes = "Aromatic herb infusion")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Thermal Equalize & Pre-Warm", 15, 80, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Rough Flour & Starter Incorporation", 10, 80, 0, 45),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Autolyse Flour Hydration Pause", 20, 80, 0, 0, hasIngredientAlert = true, alertTitle = "Autolyse: Add Salt"),
                BakingStage(BakingStageType.KNEAD_2, "Gluten Matrix & Olive Addition", 20, 82, 0, 110, hasIngredientAlert = true, alertTitle = "Dispenser Chime: Add Olives & Rosemary"),
                BakingStage(BakingStageType.RISE_1, "First Bulk Fermentation", 50, 84, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Degas & Paddle Removal Chime", 3, 84, 0, 30, hasIngredientAlert = true, alertTitle = "Extract Kneading Paddles"),
                BakingStage(BakingStageType.RISE_2, "Intermediate Rise & Olive Settle", 30, 86, 25, 0),
                BakingStage(BakingStageType.RISE_3, "Final Loaf Crown Proof", 40, 90, 35, 0),
                BakingStage(BakingStageType.BAKE, "Dual-Heater Hearth Crust Bake", 47, 400, 95, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Crust Crisp Cooling Vent", 10, 140, 10, 0)
            ),
            bakerNotes = listOf(
                "Pat kalamata olives completely dry with paper towels to prevent excess moisture pockets.",
                "High 74% hydration produces a light, open, chewy artisan crumb."
            )
        ),
        CustomRecipeEntity(
            id = "custom_wildflower_honey_spelt",
            title = "Wildflower Honey & Ancient Spelt Loaf",
            subtitle = "Custom Recipe • 100% Ancient Grain Hybrid",
            description = "Custom whole grain formula combining nutty ancient spelt flour with wildflower honey and cultured buttermilk for a soft sandwich crumb.",
            category = FlourCategory.SPELT_ANCIENT,
            primaryFlour = "Organic Stoneground Spelt Flour (350g)",
            specialtyFlours = listOf("Unbleached Bread Flour (150g)"),
            yeastType = YeastType.INSTANT_YEAST,
            defaultLoafSize = LoafSize.ONE_HALF_LB,
            defaultCrust = CrustShade.MEDIUM,
            hydrationPercent = 68,
            totalTimeMinutes = 210,
            ingredients = listOf(
                RecipeIngredient("chs_1", "Cultured Buttermilk (lukewarm)", "200 ml", 40f, BakingStageType.PREHEAT, notes = "Lactic acidity tenderizes spelt"),
                RecipeIngredient("chs_2", "Warm Water (85°F)", "140 ml", 28f, BakingStageType.PREHEAT, notes = "Hydration base"),
                RecipeIngredient("chs_3", "Raw Wildflower Honey", "35 g", 7f, BakingStageType.PREHEAT, notes = "Floral aroma & yeast food"),
                RecipeIngredient("chs_4", "Organic Whole Spelt Flour", "350 g", 70f, BakingStageType.PREHEAT, notes = "Nutty ancient grain"),
                RecipeIngredient("chs_5", "Strong Bread Flour", "150 g", 30f, BakingStageType.PREHEAT, notes = "Supports delicate spelt gluten"),
                RecipeIngredient("chs_6", "Instant Bread Machine Yeast", "6 g", 1.2f, BakingStageType.PREHEAT, notes = "Even rapid lift"),
                RecipeIngredient("chs_7", "Fine Sea Salt", "8 g", 1.6f, BakingStageType.PREHEAT, notes = "Flavor balance"),
                RecipeIngredient("chs_8", "Grass-Fed Unsalted Butter", "25 g", 5f, BakingStageType.KNEAD_2, notes = "Enriched tenderness")
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Spelt Tempering & Pre-warm", 15, 78, 10, 0),
                BakingStage(BakingStageType.KNEAD_1, "Gentle Spelt Flour Blend", 8, 78, 0, 40),
                BakingStage(BakingStageType.REST_AUTOLYSE, "Grain Hydration Rest", 12, 78, 0, 0),
                BakingStage(BakingStageType.KNEAD_2, "Gentle Knead (Do Not Over-work Spelt)", 15, 80, 0, 80, hasIngredientAlert = true, alertTitle = "Add Softened Butter"),
                BakingStage(BakingStageType.RISE_1, "Honey Bulk Rise", 45, 84, 20, 0),
                BakingStage(BakingStageType.PUNCH_DOWN, "Gentle Paddle Extraction", 3, 84, 0, 25),
                BakingStage(BakingStageType.RISE_2, "Intermediate Rest", 25, 86, 20, 0),
                BakingStage(BakingStageType.RISE_3, "Final Spelt Crown Rise", 42, 88, 30, 0),
                BakingStage(BakingStageType.BAKE, "Golden Caramel Honey Bake", 40, 360, 85, 0),
                BakingStage(BakingStageType.KEEP_WARM, "Moisture Retention Vent", 10, 135, 10, 0)
            ),
            bakerNotes = listOf(
                "Spelt gluten is more fragile than standard wheat. Keep knead gentle to avoid breaking gluten bonds.",
                "Honey caramelizes readily; medium crust setting gives a golden finish."
            )
        )
    )
}
