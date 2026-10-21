package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BreadCycleTrackingDao
import com.example.data.local.BreadMachineCycleTrackerEntity
import com.example.data.local.CustomRecipeDao
import com.example.data.local.CustomRecipeEntity
import com.example.data.local.FavoriteRecipeDao
import com.example.data.local.FavoriteRecipeEntity
import com.example.data.local.RecipeDao
import com.example.data.local.RecipeEntity
import com.example.data.model.BreadRecipe
import com.example.data.model.PreloadedBreadMachineCycles
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val customRecipeDao: CustomRecipeDao,
    private val favoriteRecipeDao: FavoriteRecipeDao,
    private val cycleTrackingDao: BreadCycleTrackingDao
) {

    // Reactive flow of active bread machine cycle tracker persisted in Room
    val activeCycleTracker: Flow<BreadMachineCycleTrackerEntity?> =
        cycleTrackingDao.getCycleFlow()

    // Direct Room query for sourdough recipes combined with persistent favorites & bake stats
    val sourdoughRecipes: Flow<List<BreadRecipe>> = combine(
        recipeDao.getSourdoughRecipes(),
        favoriteRecipeDao.getAllFavorites()
    ) { entities, favorites ->
        entities.toDomainWithFavorites(favorites)
    }

    // All recipes from Room combined with persistent favorites & bake stats
    val allRecipes: Flow<List<BreadRecipe>> = combine(
        recipeDao.getAllRecipes(),
        favoriteRecipeDao.getAllFavorites()
    ) { entities, favorites ->
        entities.toDomainWithFavorites(favorites)
    }

    // Custom bread recipes stored in Room combined with persistent favorites & bake stats
    val customRecipes: Flow<List<BreadRecipe>> = combine(
        customRecipeDao.getAllCustomRecipes(),
        favoriteRecipeDao.getAllFavorites()
    ) { entities, favorites ->
        if (entities.isEmpty()) return@combine emptyList()
        val favMap = if (favorites.isNotEmpty()) favorites.associateBy { it.recipeId } else emptyMap()
        entities.map { entity ->
            val fav = favMap[entity.id]
            if (fav != null) {
                entity.toDomain().copy(
                    isFavorite = fav.isFavorite,
                    bakeCount = fav.bakeCount
                )
            } else {
                entity.toDomain()
            }
        }
    }

    // Direct flow of favorite recipes, preserving SQLite indexed sort order
    val favoriteRecipes: Flow<List<BreadRecipe>> = combine(
        allRecipes,
        favoriteRecipeDao.getFavoriteRecipesList()
    ) { all, favList ->
        if (favList.isEmpty() || all.isEmpty()) return@combine emptyList()
        val recipeMap = all.associateBy { it.id }
        favList.mapNotNull { fav -> recipeMap[fav.recipeId] }
    }

    /**
     * Query recipes by category directly from Room
     */
    fun getRecipesByCategory(category: com.example.data.model.RecipeCategory): Flow<List<BreadRecipe>> {
        return combine(
            recipeDao.getRecipesByCategory(category),
            favoriteRecipeDao.getAllFavorites()
        ) { entities, favorites ->
            entities.toDomainWithFavorites(favorites)
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
            if (entities.isEmpty()) return@combine emptyList()
            val favMap = if (favorites.isNotEmpty()) favorites.associateBy { it.recipeId } else emptyMap()
            entities.map { entity ->
                val fav = favMap[entity.id]
                if (fav != null) {
                    entity.toDomain().copy(
                        isFavorite = fav.isFavorite,
                        bakeCount = fav.bakeCount
                    )
                } else {
                    entity.toDomain()
                }
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
        if (recipeDao.getRecipeCount() == 0 || customRecipeDao.getCustomRecipeCount() == 0 || favoriteRecipeDao.getFavoriteCount() == 0 || cycleTrackingDao.getCycleCount() == 0) {
            AppDatabase.seedInitialRecipes(recipeDao, customRecipeDao, favoriteRecipeDao, cycleTrackingDao)
        }
    }

    // --- Bread Machine Cycle Tracking Room Operations ---

    suspend fun getActiveCycleTracker(): BreadMachineCycleTrackerEntity? {
        return cycleTrackingDao.getCycle()
    }

    suspend fun saveOrUpdateCycleTracker(entity: BreadMachineCycleTrackerEntity) {
        cycleTrackingDao.insertOrUpdate(entity)
    }

    suspend fun updateCycleStatus(cycleId: String, status: String) {
        cycleTrackingDao.updateStatus(cycleId, status)
    }

    suspend fun updateCycleProgress(cycleId: String, stageIndex: Int, stageElapsed: Int, totalElapsed: Int) {
        cycleTrackingDao.updateProgress(cycleId, stageIndex, stageElapsed, totalElapsed)
    }

    suspend fun updateAddInAlarm(cycleId: String, triggered: Boolean, acknowledged: Boolean) {
        cycleTrackingDao.updateAddInAlarm(cycleId, triggered, acknowledged)
    }

    suspend fun updateAddInItems(cycleId: String, items: List<String>) {
        cycleTrackingDao.updateAddInItems(cycleId, items)
    }

    suspend fun updateAddInChimeSettings(cycleId: String, chimeSound: Boolean, vibrate: Boolean) {
        cycleTrackingDao.updateAddInChimeSettings(cycleId, chimeSound, vibrate)
    }

    suspend fun updateSimulationSpeed(cycleId: String, speed: Int) {
        cycleTrackingDao.updateSimulationSpeed(cycleId, speed)
    }

    suspend fun resetCycleTracker(cycleId: String = BreadMachineCycleTrackerEntity.DEFAULT_TRACKER_ID) {
        val current = cycleTrackingDao.getCycle(cycleId) ?: return
        val firstStage = current.stages.firstOrNull()
        val firstStageSecs = (firstStage?.durationMinutes ?: 0) * 60
        val resetEntity = current.copy(
            status = BreadMachineCycleTrackerEntity.STATUS_IDLE,
            currentStageIndex = 0,
            stageElapsedSeconds = 0,
            stageDurationSeconds = firstStageSecs,
            totalElapsedSeconds = 0,
            addInAlarmTriggered = false,
            addInAlarmAcknowledged = false,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        cycleTrackingDao.insertOrUpdate(resetEntity)
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

private fun List<RecipeEntity>.toDomainWithFavorites(favorites: List<FavoriteRecipeEntity>): List<BreadRecipe> {
    if (this.isEmpty()) return emptyList()
    if (favorites.isEmpty()) return this.map { it.toDomain() }
    val favMap = favorites.associateBy { it.recipeId }
    return this.map { entity ->
        val fav = favMap[entity.id]
        if (fav != null) {
            entity.toDomain().copy(
                isFavorite = fav.isFavorite,
                bakeCount = fav.bakeCount
            )
        } else {
            entity.toDomain()
        }
    }
}

