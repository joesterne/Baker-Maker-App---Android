package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.PreloadedRecipes
import com.example.data.local.RecipeDao
import com.example.data.local.RecipeEntity
import com.example.data.model.BakingStageType
import com.example.data.model.BreadMakerModel
import com.example.data.model.FlourCategory
import com.example.data.model.ZojirushiMenuCourse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: AppDatabase
  private lateinit var dao: RecipeDao

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    dao = db.recipeDao()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CrustCraft", appName)
  }

  @Test
  fun `room database seeds and queries sourdough recipes`() = runBlocking {
    // Seed initial recipes into in-memory Room DB
    val entities = PreloadedRecipes.RECIPES.map { RecipeEntity.fromDomain(it) }
    dao.insertAll(entities)

    val count = dao.getRecipeCount()
    assertTrue("Database should have inserted recipes", count > 0)

    // Query specifically sourdough recipes from Room DAO
    val sourdoughList = dao.getSourdoughRecipes().first()
    assertTrue("Should retrieve sourdough recipes from Room", sourdoughList.isNotEmpty())

    // Verify all returned are sourdough
    sourdoughList.forEach { entity ->
      assertTrue(
        "Recipe ${entity.title} should be sourdough",
        entity.category == FlourCategory.SOURDOUGH || entity.isSourdough
      )
    }

    // Verify SF Sourdough exists and has hydration 70%
    val sf = sourdoughList.find { it.id == "sf_artisan_sourdough" }
    assertNotNull(sf)
    assertEquals(70, sf?.hydrationPercent)
  }

  @Test
  fun `verify zojirushi bb-pdc20 model specifications and dedicated recipe`() {
    val zojirushiModel = BreadMakerModel.ZOJIRUSHI_BB_PDC20
    assertEquals(2, zojirushiModel.bladeCount)
    assertTrue("Zojirushi BB-PDC20 should have upper lid heater", zojirushiModel.hasLidHeater)
    assertTrue("Zojirushi BB-PDC20 should support homemade memory banks", zojirushiModel.hasHomemadeMemory)

    // Verify Zojirushi dedicated sourdough recipe
    val recipes = PreloadedRecipes.SOURDOUGH_RECIPES
    val zojirushiRecipe = recipes.find { it.id == "zojirushi_virtuoso_sourdough" }
    assertNotNull(zojirushiRecipe)
    assertEquals(ZojirushiMenuCourse.EUROPEAN, zojirushiRecipe?.zojirushiRecommendedCourse)
    assertEquals(72, zojirushiRecipe?.hydrationPercent)

    // Verify twin paddle removal alert exists in punch down stage
    val punchDownStage = zojirushiRecipe?.stages?.find { it.type == BakingStageType.PUNCH_DOWN }
    assertNotNull(punchDownStage)
    assertTrue("Punch down stage should prompt for blade removal", punchDownStage?.hasIngredientAlert == true)
  }

  @Test
  fun `verify preloaded sourdough recipes contain ingredient alert stages`() {
    val recipes = PreloadedRecipes.RECIPES
    assertTrue("Preloaded recipes should not be empty", recipes.isNotEmpty())

    val sfSourdough = recipes.find { it.id == "sf_artisan_sourdough" }
    assertNotNull(sfSourdough)
    assertEquals(70, sfSourdough?.hydrationPercent)

    // Verify ingredient alerts exist in stages
    val hasAlert = sfSourdough?.stages?.any { it.hasIngredientAlert } == true
    assertTrue("Sourdough should have ingredient alert stages", hasAlert)

    // Verify Knead 2 or dispenser chime exists
    val knead2Stage = sfSourdough?.stages?.find { it.type == BakingStageType.KNEAD_2 }
    assertNotNull(knead2Stage)
  }
}
