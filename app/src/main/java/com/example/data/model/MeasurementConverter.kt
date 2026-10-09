package com.example.data.model

import java.util.Locale

/**
 * Common Baking Ingredient Densities for exact Grams ↔ Milliliters (mL) conversion.
 * Formula: mL = grams / density, grams = mL * density
 */
enum class LiquidDensityItem(
    val displayName: String,
    val densityGramsPerMl: Double,
    val note: String,
    val defaultTypicalAmount: Double = 250.0
) {
    WATER("Water (Room Temp)", 1.000, "1 g = 1.00 mL (Standard base ratio)", 250.0),
    WHOLE_MILK("Whole Milk (3.25% fat)", 1.030, "100 g = 97.1 mL (1 cup = 247 g)", 240.0),
    SKIM_MILK("Skim / Low-Fat Milk", 1.033, "100 g = 96.8 mL", 240.0),
    BUTTERMILK("Cultured Buttermilk", 1.030, "100 g = 97.1 mL (Thick acidic dairy)", 240.0),
    VEGETABLE_OIL("Vegetable / Canola Oil", 0.920, "100 g = 108.7 mL (Lighter than water)", 30.0),
    OLIVE_OIL("Extra Virgin Olive Oil", 0.918, "100 g = 108.9 mL (1 tbsp = 13.8 g)", 30.0),
    SOURDOUGH_STARTER("Sourdough Starter (100% Hydr.)", 1.020, "100 g = 98.0 mL (Equal flour & water)", 150.0),
    HONEY("Pure Honey", 1.420, "100 g = 70.4 mL (Dense sugar; 1 tbsp = 21.3 g)", 40.0),
    MAPLE_SYRUP("Pure Maple Syrup", 1.370, "100 g = 73.0 mL (Natural syrup)", 40.0),
    MOLASSES("Dark Molasses", 1.400, "100 g = 71.4 mL (Pumpernickel & dark breads)", 30.0),
    MELTED_BUTTER("Melted Butter", 0.911, "100 g = 109.8 mL (Brioche & enrichments)", 50.0),
    EGG("Beaten Whole Egg", 1.031, "1 large egg ≈ 50 g ≈ 48.5 mL", 50.0)
}

/**
 * Common Flour & Dry Ingredient Densities (Cups to Grams)
 */
enum class FlourCupDensity(
    val displayName: String,
    val gramsPerCup: Double,
    val proteinContent: String,
    val notes: String
) {
    BREAD_FLOUR("Unbleached Bread Flour", 127.0, "12.7%", "High gluten King Arthur / Bob's Red Mill standard"),
    ALL_PURPOSE_FLOUR("All-Purpose Flour", 120.0, "11.7%", "Standard all-around bleached/unbleached flour"),
    WHOLE_WHEAT_FLOUR("100% Whole Wheat Flour", 120.0, "13.0%", "Stoneground whole grain (absorbs +5-10% water)"),
    DARK_RYE_FLOUR("Dark Rye Flour", 102.0, "10.0%", "Dense artisanal European grain, high enzyme"),
    SPELT_FLOUR("Ancient Spelt Flour", 115.0, "12.5%", "Delicate extensible gluten structure"),
    GRANULATED_SUGAR("White Granulated Sugar", 200.0, "0%", "Sweetener & yeast nutrient"),
    BROWN_SUGAR("Packed Brown Sugar", 220.0, "0%", "Moist molasses sugar for rich crusts"),
    BUTTER_CUP("Butter (1 cup / 2 sticks)", 227.0, "80% fat", "1 stick = 8 tbsp = 113.5 g")
}

/**
 * Common Spoon Measurements (Teaspoons & Tablespoons)
 */
enum class SpoonMeasurement(
    val displayName: String,
    val gramsPerTsp: Double,
    val gramsPerTbsp: Double,
    val note: String
) {
    INSTANT_YEAST("Instant / Bread Machine Yeast", 3.1, 9.3, "1 packet = 2¼ tsp = 7.0 g"),
    ACTIVE_DRY_YEAST("Active Dry Yeast", 2.8, 8.4, "1 packet = 2¼ tsp = 7.0 g (needs blooming)"),
    TABLE_SALT("Fine Table Salt (Iodized)", 5.7, 17.1, "Standard bread recipe salt weight"),
    FINE_SEA_SALT("Fine Sea Salt", 5.0, 15.0, "Unrefined sea salt crystals"),
    DIAMOND_KOSHER_SALT("Diamond Crystal Kosher Salt", 2.8, 8.5, "Flaky coarse salt; twice the volume of table salt"),
    WHITE_SUGAR("Granulated Sugar", 4.2, 12.5, "Standard baking sugar"),
    HONEY_SPOON("Pure Honey", 7.1, 21.3, "1 tbsp = 21.3 g"),
    OLIVE_OIL_SPOON("Olive / Vegetable Oil", 4.6, 13.8, "1 tbsp = 15 mL = 13.8 g"),
    WATER_SPOON("Water / Milk", 5.0, 15.0, "1 tsp = 5 mL = 5 g; 1 tbsp = 15 mL = 15 g")
}

enum class UnitSystem(val label: String, val shortLabel: String, val description: String) {
    METRIC("Metric (Grams & mL)", "g / mL", "Precise scale baking for repeatable artisanal crumb"),
    IMPERIAL("Imperial (Cups, Tbsp, oz)", "cups / oz", "Traditional volume measurements from American books"),
    DUAL("Dual Display (Both)", "g & cups", "Shows grams with approximate volume equivalents")
}

data class ConverterSettings(
    val preferredUnitSystem: UnitSystem = UnitSystem.METRIC,
    val defaultLiquid: LiquidDensityItem = LiquidDensityItem.WATER,
    val cupStandardMl: Double = 240.0, // 240 mL US Legal vs 250 mL Metric Cup
    val showCheatSheetOnHome: Boolean = true,
    val autoDisplayHydrationPercentage: Boolean = true
)

object BakingConverter {

    /**
     * Converts grams to milliliters for a given liquid
     * Formula: mL = grams / density
     */
    fun gramsToMl(grams: Double, liquid: LiquidDensityItem): Double {
        if (grams <= 0.0 || liquid.densityGramsPerMl <= 0.0) return 0.0
        return grams / liquid.densityGramsPerMl
    }

    /**
     * Converts milliliters to grams for a given liquid
     * Formula: grams = mL * density
     */
    fun mlToGrams(ml: Double, liquid: LiquidDensityItem): Double {
        if (ml <= 0.0 || liquid.densityGramsPerMl <= 0.0) return 0.0
        return ml * liquid.densityGramsPerMl
    }

    /**
     * Converts cups of flour/dry ingredient to grams
     */
    fun cupsToGrams(cups: Double, flour: FlourCupDensity): Double {
        if (cups <= 0.0) return 0.0
        return cups * flour.gramsPerCup
    }

    /**
     * Converts grams of flour to cups
     */
    fun gramsToCups(grams: Double, flour: FlourCupDensity): Double {
        if (grams <= 0.0 || flour.gramsPerCup <= 0.0) return 0.0
        return grams / flour.gramsPerCup
    }

    /**
     * Calculate Baker's Hydration Percentage:
     * (Total Liquid Weight / Total Flour Weight) * 100
     */
    fun calculateHydration(flourGrams: Double, liquidGrams: Double): Double {
        if (flourGrams <= 0.0) return 0.0
        return (liquidGrams / flourGrams) * 100.0
    }

    /**
     * Yeast equivalents:
     * 1 g Instant Dry Yeast = 1.25 g Active Dry Yeast = 3.0 g Fresh Cake Yeast
     */
    fun instantToActiveDry(instantGrams: Double): Double = instantGrams * 1.25
    fun instantToFreshCake(instantGrams: Double): Double = instantGrams * 3.0
    fun activeDryToInstant(activeDryGrams: Double): Double = activeDryGrams / 1.25
    fun freshCakeToInstant(freshGrams: Double): Double = freshGrams / 3.0

    /**
     * Format decimal numbers cleanly (e.g. 1 decimal place)
     */
    fun formatDouble(value: Double): String {
        return if (value % 1.0 == 0.0) {
            String.format(Locale.US, "%.0f", value)
        } else {
            String.format(Locale.US, "%.1f", value)
        }
    }
}
