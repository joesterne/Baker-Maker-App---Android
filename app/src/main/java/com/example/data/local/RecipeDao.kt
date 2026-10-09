package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RecipeCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE category = 'SOURDOUGH' OR isSourdough = 1 OR yeastType = 'SOURDOUGH_STARTER' ORDER BY hydrationPercent DESC, title ASC")
    fun getSourdoughRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY createdAt DESC, title ASC")
    fun getRecipesByCategory(category: RecipeCategory): Flow<List<RecipeEntity>>

    @Query("SELECT COUNT(*) FROM recipes WHERE category = :category")
    suspend fun getRecipeCountByCategory(category: RecipeCategory): Int

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    @Query("UPDATE recipes SET photoUri = :photoUri WHERE id = :id")
    suspend fun updateRecipePhoto(id: String, photoUri: String?)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: String)
}
