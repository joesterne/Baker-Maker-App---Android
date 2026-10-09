package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.CustomRecipeDao
import com.example.data.local.CustomRecipeEntity
import com.example.data.local.RecipeDao
import com.example.data.local.RecipeEntity
import com.example.data.model.BreadRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val customRecipeDao: CustomRecipeDao
) {

    // Direct Room query for sourdough recipes
    val sourdoughRecipes: Flow<List<BreadRecipe>> = recipeDao.getSourdoughRecipes().map { entities ->
        entities.map { it.toDomain() }
    }

    // All recipes from Room
    val allRecipes: Flow<List<BreadRecipe>> = recipeDao.getAllRecipes().map { entities ->
        entities.map { it.toDomain() }
    }

    // Custom bread recipes stored in the custom_recipes Room table
    val customRecipes: Flow<List<BreadRecipe>> = customRecipeDao.getAllCustomRecipes().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Query recipes by category directly from Room
     */
    fun getRecipesByCategory(category: com.example.data.model.RecipeCategory): Flow<List<BreadRecipe>> {
        return recipeDao.getRecipesByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Query custom recipes by category directly from Room
     */
    fun getCustomRecipesByCategory(category: com.example.data.model.RecipeCategory): Flow<List<BreadRecipe>> {
        return customRecipeDao.getCustomRecipesByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Search custom recipes directly via Room SQL query
     */
    fun searchCustomRecipes(query: String): Flow<List<BreadRecipe>> {
        return customRecipeDao.searchCustomRecipes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun ensureSeeded() {
        if (recipeDao.getRecipeCount() == 0 || customRecipeDao.getCustomRecipeCount() == 0) {
            AppDatabase.seedInitialRecipes(recipeDao, customRecipeDao)
        }
    }

    suspend fun saveRecipe(recipe: BreadRecipe) {
        val entity = RecipeEntity.fromDomain(recipe)
        recipeDao.insertRecipe(entity)
        if (recipe.isCustom) {
            val customEntity = CustomRecipeEntity.fromDomain(recipe)
            customRecipeDao.insertCustomRecipe(customEntity)
        }
    }

    suspend fun saveCustomRecipe(recipe: BreadRecipe) {
        val customEntity = CustomRecipeEntity.fromDomain(recipe)
        customRecipeDao.insertCustomRecipe(customEntity)
        val entity = RecipeEntity.fromDomain(recipe)
        recipeDao.insertRecipe(entity)
    }

    suspend fun updateRecipePhoto(id: String, photoUri: String?) {
        recipeDao.updateRecipePhoto(id, photoUri)
        customRecipeDao.updateCustomRecipePhoto(id, photoUri)
    }

    suspend fun deleteRecipe(id: String) {
        recipeDao.deleteRecipeById(id)
        customRecipeDao.deleteCustomRecipeById(id)
    }

    suspend fun deleteCustomRecipe(id: String) {
        customRecipeDao.deleteCustomRecipeById(id)
        recipeDao.deleteRecipeById(id)
    }

    suspend fun getRecipeById(id: String): BreadRecipe? {
        val custom = customRecipeDao.getCustomRecipeById(id)
        if (custom != null) return custom.toDomain()
        return recipeDao.getRecipeById(id)?.toDomain()
    }
}
