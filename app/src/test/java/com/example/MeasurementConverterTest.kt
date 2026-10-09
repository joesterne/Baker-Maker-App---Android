package com.example

import com.example.data.model.BakingConverter
import com.example.data.model.FlourCupDensity
import com.example.data.model.LiquidDensityItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MeasurementConverterTest {

    @Test
    fun testGramsToMl_water() {
        val ml = BakingConverter.gramsToMl(240.0, LiquidDensityItem.WATER)
        assertEquals(240.0, ml, 0.001)
    }

    @Test
    fun testGramsToMl_wholeMilk() {
        // Whole milk density = 1.03 g/mL. 240g milk should be ~233.0 mL
        val ml = BakingConverter.gramsToMl(240.0, LiquidDensityItem.WHOLE_MILK)
        assertEquals(233.009, ml, 0.01)
    }

    @Test
    fun testGramsToMl_vegetableOil() {
        // Vegetable oil density = 0.92 g/mL. 100g oil is ~108.7 mL
        val ml = BakingConverter.gramsToMl(100.0, LiquidDensityItem.VEGETABLE_OIL)
        assertEquals(108.695, ml, 0.01)
    }

    @Test
    fun testMlToGrams_water() {
        val grams = BakingConverter.mlToGrams(360.0, LiquidDensityItem.WATER)
        assertEquals(360.0, grams, 0.001)
    }

    @Test
    fun testMlToGrams_honey() {
        // Honey density = 1.42 g/mL. 100 mL of honey = 142g
        val grams = BakingConverter.mlToGrams(100.0, LiquidDensityItem.HONEY)
        assertEquals(142.0, grams, 0.01)
    }

    @Test
    fun testCupsToGrams_breadFlour() {
        // 3 cups of bread flour (127g per cup) = 381g
        val grams = BakingConverter.cupsToGrams(3.0, FlourCupDensity.BREAD_FLOUR)
        assertEquals(381.0, grams, 0.01)
    }

    @Test
    fun testHydrationPercentage() {
        // 500g flour + 360g water = 72% hydration
        val hydration = BakingConverter.calculateHydration(500.0, 360.0)
        assertEquals(72.0, hydration, 0.01)
    }

    @Test
    fun testYeastEquivalents() {
        // 7g instant yeast = 8.75g active dry
        val activeDry = BakingConverter.instantToActiveDry(7.0)
        assertEquals(8.75, activeDry, 0.01)

        // 7g instant yeast = 21g fresh cake yeast
        val freshCake = BakingConverter.instantToFreshCake(7.0)
        assertEquals(21.0, freshCake, 0.01)
    }
}
