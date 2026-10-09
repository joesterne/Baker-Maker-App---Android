package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.CustomRecipeDao
import com.example.data.local.CustomRecipeEntity
import com.example.data.local.PreloadedCustomRecipes
import com.example.data.model.BakingStage
import com.example.data.model.BakingStageType
import com.example.data.model.CrustShade
import com.example.data.model.RecipeCategory
import com.example.data.model.LoafSize
import com.example.data.model.RecipeIngredient
import com.example.data.model.YeastType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CustomRecipeRoomDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var customDao: CustomRecipeDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        customDao = db.customRecipeDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `custom bread recipes insert and query successfully with ingredients and baking stages`() = runBlocking {
        // Seed preloaded custom recipes
        customDao.insertAllCustomRecipes(PreloadedCustomRecipes.INITIAL_CUSTOM_RECIPES)

        val count = customDao.getCustomRecipeCount()
        assertEquals(3, count)

        val recipes = customDao.getAllCustomRecipes().first()
        assertEquals(3, recipes.size)

        val oliveRecipe = recipes.firstOrNull { it.id == "custom_rosemary_olive_artisan" }
        assertNotNull(oliveRecipe)
        assertEquals("Rosemary & Kalamata Olive Artisan Loaf", oliveRecipe!!.title)
        assertEquals(74, oliveRecipe.hydrationPercent)
        assertEquals(245, oliveRecipe.totalTimeMinutes) // Duration data

        // Verify ingredients data
        assertTrue(oliveRecipe.ingredients.isNotEmpty())
        assertEquals(8, oliveRecipe.ingredients.size)
        val olives = oliveRecipe.ingredients.find { it.id == "cro_7" }
        assertNotNull(olives)
        assertEquals("Pitted Kalamata Olives (drained)", olives!!.name)
        assertTrue(olives.isAutoDispenser)

        // Verify baking stages data
        assertTrue(oliveRecipe.stages.isNotEmpty())
        assertEquals(10, oliveRecipe.stages.size)
        val bakeStage = oliveRecipe.stages.find { it.type == BakingStageType.BAKE }
        assertNotNull(bakeStage)
        assertEquals(47, bakeStage!!.durationMinutes)
        assertEquals(400, bakeStage.targetTempF)
    }

    @Test
    fun `search custom recipes by query matches title, flour, and description`() = runBlocking {
        customDao.insertAllCustomRecipes(PreloadedCustomRecipes.INITIAL_CUSTOM_RECIPES)

        // Search by title keyword "Olive"
        val searchOlive = customDao.searchCustomRecipes("Olive").first()
        assertEquals(1, searchOlive.size)
        assertEquals("Rosemary & Kalamata Olive Artisan Loaf", searchOlive[0].title)

        // Search by flour keyword "Spelt"
        val searchSpelt = customDao.searchCustomRecipes("Spelt").first()
        assertEquals(1, searchSpelt.size)
        assertEquals("Wildflower Honey & Ancient Spelt Loaf", searchSpelt[0].title)

        // Search by nonexistent term
        val searchNone = customDao.searchCustomRecipes("BriocheNonExistent").first()
        assertTrue(searchNone.isEmpty())
    }

    @Test
    fun `custom recipe insert, update, and delete operations`() = runBlocking {
        val testRecipe = CustomRecipeEntity(
            id = "custom_test_jalapeno_cheddar",
            title = "Jalapeño Cheddar Sourdough",
            subtitle = "Spicy artisan cheese loaf",
            description = "Sharp cheddar and diced jalapeños in a tangy sourdough base.",
            category = RecipeCategory.SOURDOUGH,
            primaryFlour = "Bread Flour (450g)",
            specialtyFlours = listOf("Sharp White Cheddar (100g)"),
            yeastType = YeastType.SOURDOUGH_STARTER,
            defaultLoafSize = LoafSize.TWO_LB,
            defaultCrust = CrustShade.ARTISAN_CRISPY,
            hydrationPercent = 70,
            totalTimeMinutes = 230,
            ingredients = listOf(
                RecipeIngredient("jc1", "Water", "315 ml", 70f, BakingStageType.PREHEAT),
                RecipeIngredient("jc2", "Bread Flour", "450 g", 100f, BakingStageType.PREHEAT)
            ),
            stages = listOf(
                BakingStage(BakingStageType.PREHEAT, "Warm pan", 15, 80),
                BakingStage(BakingStageType.BAKE, "Bake loaf", 45, 390)
            ),
            bakerNotes = listOf("Fold cheese in gently.")
        )

        // Insert
        customDao.insertCustomRecipe(testRecipe)
        var fetched = customDao.getCustomRecipeById("custom_test_jalapeno_cheddar")
        assertNotNull(fetched)
        assertEquals("Jalapeño Cheddar Sourdough", fetched!!.title)
        assertEquals(230, fetched.totalTimeMinutes)

        // Update
        val updated = fetched.copy(totalTimeMinutes = 240, title = "Extra Sharp Jalapeño Cheddar")
        customDao.updateCustomRecipe(updated)
        val fetchedUpdated = customDao.getCustomRecipeById("custom_test_jalapeno_cheddar")
        assertEquals("Extra Sharp Jalapeño Cheddar", fetchedUpdated!!.title)
        assertEquals(240, fetchedUpdated.totalTimeMinutes)

        // Delete
        customDao.deleteCustomRecipeById("custom_test_jalapeno_cheddar")
        val fetchedDeleted = customDao.getCustomRecipeById("custom_test_jalapeno_cheddar")
        assertNull(fetchedDeleted)
    }

    @Test
    fun `categorizing recipes in Room supports Sourdough, Yeast Bread, and Sweet Loaves`() = runBlocking {
        customDao.insertAllCustomRecipes(PreloadedCustomRecipes.INITIAL_CUSTOM_RECIPES)

        // Sourdough category check
        val sourdoughList = customDao.getCustomRecipesByCategory(RecipeCategory.SOURDOUGH).first()
        assertEquals(1, sourdoughList.size)
        assertEquals("Rosemary & Kalamata Olive Artisan Loaf", sourdoughList[0].title)
        assertEquals(RecipeCategory.SOURDOUGH, sourdoughList[0].category)

        // Sweet Loaves category check
        val sweetLoavesList = customDao.getCustomRecipesByCategory(RecipeCategory.SWEET_LOAVES).first()
        assertEquals(1, sweetLoavesList.size)
        assertEquals("Wildflower Honey & Ancient Spelt Loaf", sweetLoavesList[0].title)
        assertEquals(RecipeCategory.SWEET_LOAVES, sweetLoavesList[0].category)

        // Yeast Bread category check
        val yeastBreadList = customDao.getCustomRecipesByCategory(RecipeCategory.YEAST_BREAD).first()
        assertEquals(1, yeastBreadList.size)
        assertEquals("Rustic Roasted Garlic & Herb White Bread", yeastBreadList[0].title)
        assertEquals(RecipeCategory.YEAST_BREAD, yeastBreadList[0].category)

        // Verify count by category
        assertEquals(1, customDao.getCustomRecipeCountByCategory(RecipeCategory.SOURDOUGH))
        assertEquals(1, customDao.getCustomRecipeCountByCategory(RecipeCategory.SWEET_LOAVES))
        assertEquals(1, customDao.getCustomRecipeCountByCategory(RecipeCategory.YEAST_BREAD))
    }

    @Test
    fun `loaf photo uri persists and updates in Room database`() = runBlocking {
        customDao.insertAllCustomRecipes(PreloadedCustomRecipes.INITIAL_CUSTOM_RECIPES)
        val recipe = customDao.getCustomRecipeById("custom_rosemary_olive_artisan")
        assertNotNull(recipe)
        assertNull(recipe!!.photoUri)

        val testPhotoUri = "content://com.example.fileprovider/loaf_photos/loaf_rosemary_12345.jpg"
        customDao.updateCustomRecipePhoto("custom_rosemary_olive_artisan", testPhotoUri)

        val updated = customDao.getCustomRecipeById("custom_rosemary_olive_artisan")
        assertNotNull(updated)
        assertEquals(testPhotoUri, updated!!.photoUri)

        // Clear photo
        customDao.updateCustomRecipePhoto("custom_rosemary_olive_artisan", null)
        val cleared = customDao.getCustomRecipeById("custom_rosemary_olive_artisan")
        assertNotNull(cleared)
        assertNull(cleared!!.photoUri)
    }
}
