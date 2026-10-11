package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRecipeDao {
    @Query("SELECT * FROM favorite_recipes ORDER BY isFavorite DESC, bakeCount DESC, favoritedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteRecipeEntity>>

    @Query("SELECT * FROM favorite_recipes WHERE isFavorite = 1 OR bakeCount > 0 ORDER BY bakeCount DESC, favoritedAt DESC")
    fun getFavoriteRecipesList(): Flow<List<FavoriteRecipeEntity>>

    @Query("SELECT * FROM favorite_recipes WHERE recipeId = :recipeId LIMIT 1")
    suspend fun getFavoriteByRecipeId(recipeId: String): FavoriteRecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(favorite: FavoriteRecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(favorites: List<FavoriteRecipeEntity>)

    @Query("UPDATE favorite_recipes SET isFavorite = :isFavorite, favoritedAt = :timestamp WHERE recipeId = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: String, isFavorite: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM favorite_recipes WHERE recipeId = :recipeId")
    suspend fun deleteFavorite(recipeId: String)

    @Query("SELECT COUNT(*) FROM favorite_recipes WHERE isFavorite = 1")
    suspend fun getFavoriteCount(): Int
}
