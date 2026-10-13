package com.example

import com.example.data.local.Converters
import com.example.data.local.PreloadedRecipes
import com.example.data.model.RecipeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.EnumMap

class SpeedOptimizationsTest {

    @Test
    fun testConvertersFastPath() {
        val converters = Converters()

        // Empty string and "[]" fast paths should return empty list directly
        assertTrue(converters.toIngredientList("").isEmpty())
        assertTrue(converters.toIngredientList(null).isEmpty())
        assertTrue(converters.toIngredientList("[]").isEmpty())

        assertTrue(converters.toStageList("").isEmpty())
        assertTrue(converters.toStageList(null).isEmpty())
        assertTrue(converters.toStageList("[]").isEmpty())

        assertTrue(converters.toStringList("").isEmpty())
        assertTrue(converters.toStringList(null).isEmpty())
        assertTrue(converters.toStringList("[]").isEmpty())

        // Serializing null or empty list fast paths
        assertEquals("[]", converters.fromIngredientList(null))
        assertEquals("[]", converters.fromIngredientList(emptyList()))
        assertEquals("[]", converters.fromStageList(null))
        assertEquals("[]", converters.fromStageList(emptyList()))
        assertEquals("[]", converters.fromStringList(null))
        assertEquals("[]", converters.fromStringList(emptyList()))
    }

    @Test
    fun testSinglePassCategoryCounts() {
        val recipes = PreloadedRecipes.RECIPES

        // Calculate using single-pass EnumMap approach
        val counts = EnumMap<RecipeCategory, Int>(RecipeCategory::class.java)
        var favCount = 0
        var customCount = 0
        for (recipe in recipes) {
            if (recipe.isFavorite || recipe.bakeCount > 0) favCount++
            if (recipe.isCustom) customCount++
            val cat = recipe.category
            counts[cat] = (counts[cat] ?: 0) + 1
        }
        counts[RecipeCategory.ALL] = recipes.size
        counts[RecipeCategory.FAVORITES] = favCount
        counts[RecipeCategory.CUSTOM] = customCount

        // Verify totals match multi-pass calculations
        assertEquals(recipes.size, counts[RecipeCategory.ALL])
        assertEquals(recipes.count { it.category == RecipeCategory.SOURDOUGH }, counts[RecipeCategory.SOURDOUGH])
        assertEquals(recipes.count { it.category == RecipeCategory.WHOLE_GRAIN }, counts[RecipeCategory.WHOLE_GRAIN])
        assertEquals(recipes.count { it.category == RecipeCategory.SWEET_LOAVES }, counts[RecipeCategory.SWEET_LOAVES])
        assertEquals(recipes.count { it.isCustom }, counts[RecipeCategory.CUSTOM])
    }
}
