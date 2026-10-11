package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.CustomRecipeDao
import com.example.data.local.CustomRecipeEntity
import com.example.data.local.FavoriteRecipeDao
import com.example.data.local.FavoriteRecipeEntity
import com.example.data.local.RecipeDao
import com.example.data.local.RecipeEntity
import com.example.data.model.BreadRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val customRecipeDao: CustomRecipeDao,
    private val favoriteRecipeDao: FavoriteRecipeDao
) {

    // Direct Room query for sourdough recipes combined with persistent favorites & bake stats
    val sourdoughRecipes: Flow<List<BreadRecipe>> = combine(
        recipeDao.getSourdoughRecipes(),
        favoriteRecipeDao.getAllFavorites()
    ) { entities, favorites ->
        val favMap = favorites.associateBy { it.recipeId }
        entities.map { entity ->
            val fav = favMap[entity.id]
            entity.toDomain().copy(
                isFavorite = fav?.isFavorite ?: false,
                bakeCount = fav?.bakeCount ?: 0
            )
        }
    }

    // All recipes from Room combined with persistent favorites & bake stats
    val allRecipes: Flow<List<BreadRecipe>> = combine(
        recipeDao.getAllRecipes(),
        favoriteRecipeDao.getAllFavorites()
    ) { entities, favorites ->
        val favMap = favorites.associateBy { it.recipeId }
        entities.map { entity ->
            val fav = favMap[entity.id]
            entity.toDomain().copy(
                isFavorite = fav?.isFavorite ?: false,
                bakeCount = fav?.bakeCount ?: 0
            )
        }
    }

    // Custom bread recipes stored in Room combined with persistent favorites & bake stats
    val customRecipes: Flow<List<BreadRecipe>> = combine(
        customRecipeDao.getAllCustomRecipes(),
        favoriteRecipeDao.getAllFavorites()
    ) { entities, favorites ->
        val favMap = favorites.associateBy { it.recipeId }
        entities.map { entity ->
            val fav = favMap[entity.id]
            entity.toDomain().copy(
                isFavorite = fav?.isFavorite ?: false,
                bakeCount = fav?.bakeCount ?: 0
            )
        }
    }

    // Direct flow of favorite recipes
    val favoriteRecipes: Flow<List<BreadRecipe>> = combine(
        allRecipes,
        favoriteRecipeDao.getFavoriteRecipesList()
    ) { all, favList ->
        val favIds = favList.map { it.recipeId }.toSet()
        all.filter { it.id in favIds }.sortedWith(
            compareByDescending<BreadRecipe> { it.bakeCount }
                .thenByDescending { it.isFavorite }
                .thenBy { it.title }
        )
    }

    /**
     * Query recipes by category directly from Room
     */
    fun getRecipesByCategory(category: com.example.data.model.RecipeCategory): Flow<List<BreadRecipe>> {
        return combine(
            recipeDao.getRecipesByCategory(category),
            favoriteRecipeDao.getAllFavorites()
        ) { entities, favorites ->
            val favMap = favorites.associateBy { it.recipeId }
            entities.map { entity ->
                val fav = favMap[entity.id]
                entity.toDomain().copy(
                    isFavorite = fav?.isFavorite ?: false,
                    bakeCount = fav?.bakeCount ?: 0
                )
            }
        }
    }

    /**
     * Search custom recipes directly via Room SQL query
     */
    fun searchCustomRecipes(query: String): Flow<List<BreadRecipe>> {
        return combine(
            customRecipeDao.searchCustomRecipes(query),
            favoriteRecipeDao.getAllFavorites()
        ) { entities, favorites ->
            val favMap = favorites.associateBy { it.recipeId }
            entities.map { entity ->
                val fav = favMap[entity.id]
                entity.toDomain().copy(
                    isFavorite = fav?.isFavorite ?: false,
                    bakeCount = fav?.bakeCount ?: 0
                )
            }
        }
    }

    suspend fun toggleFavorite(recipeId: String, currentStatus: Boolean) {
        val existing = favoriteRecipeDao.getFavoriteByRecipeId(recipeId)
        if (existing != null) {
            favoriteRecipeDao.insertOrUpdate(
                existing.copy(
                    isFavorite = !currentStatus,
                    favoritedAt = System.currentTimeMillis()
                )
            )
        } else {
            favoriteRecipeDao.insertOrUpdate(
                FavoriteRecipeEntity(
                    recipeId = recipeId,
                    isFavorite = !currentStatus,
                    bakeCount = 0,
                    favoritedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun recordBake(recipeId: String) {
        val existing = favoriteRecipeDao.getFavoriteByRecipeId(recipeId)
        if (existing != null) {
            favoriteRecipeDao.insertOrUpdate(
                existing.copy(
                    bakeCount = existing.bakeCount + 1,
                    lastBakedAt = System.currentTimeMillis()
                )
            )
        } else {
            favoriteRecipeDao.insertOrUpdate(
                FavoriteRecipeEntity(
                    recipeId = recipeId,
                    isFavorite = false,
                    bakeCount = 1,
                    lastBakedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun ensureSeeded() {
        if (recipeDao.getRecipeCount() == 0 || customRecipeDao.getCustomRecipeCount() == 0 || favoriteRecipeDao.getFavoriteCount() == 0) {
            AppDatabase.seedInitialRecipes(recipeDao, customRecipeDao, favoriteRecipeDao)
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
        favoriteRecipeDao.deleteFavorite(id)
    }

    suspend fun deleteCustomRecipe(id: String) {
        customRecipeDao.deleteCustomRecipeById(id)
        recipeDao.deleteRecipeById(id)
        favoriteRecipeDao.deleteFavorite(id)
    }

    suspend fun getRecipeById(id: String): BreadRecipe? {
        val fav = favoriteRecipeDao.getFavoriteByRecipeId(id)
        val custom = customRecipeDao.getCustomRecipeById(id)
        if (custom != null) {
            return custom.toDomain().copy(
                isFavorite = fav?.isFavorite ?: false,
                bakeCount = fav?.bakeCount ?: 0
            )
        }
        return recipeDao.getRecipeById(id)?.toDomain()?.copy(
            isFavorite = fav?.isFavorite ?: false,
            bakeCount = fav?.bakeCount ?: 0
        )
    }
}
