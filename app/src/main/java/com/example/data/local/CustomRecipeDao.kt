package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object (DAO) for storing, querying, and searching
 * custom bread recipes, including their ingredients, baking stages, and duration data.
 */
@Dao
interface CustomRecipeDao {

    @Query("SELECT * FROM custom_recipes ORDER BY createdAt DESC")
    fun getAllCustomRecipes(): Flow<List<CustomRecipeEntity>>

    @Query("""
        SELECT * FROM custom_recipes 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
           OR primaryFlour LIKE '%' || :query || '%'
           OR subtitle LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchCustomRecipes(query: String): Flow<List<CustomRecipeEntity>>

    @Query("SELECT * FROM custom_recipes WHERE id = :id LIMIT 1")
    suspend fun getCustomRecipeById(id: String): CustomRecipeEntity?

    @Query("SELECT COUNT(*) FROM custom_recipes")
    suspend fun getCustomRecipeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomRecipe(recipe: CustomRecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCustomRecipes(recipes: List<CustomRecipeEntity>)

    @Update
    suspend fun updateCustomRecipe(recipe: CustomRecipeEntity)

    @Delete
    suspend fun deleteCustomRecipe(recipe: CustomRecipeEntity)

    @Query("DELETE FROM custom_recipes WHERE id = :id")
    suspend fun deleteCustomRecipeById(id: String)
}
