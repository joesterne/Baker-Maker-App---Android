package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_recipes",
    indices = [
        Index("isFavorite"),
        Index("bakeCount")
    ]
)
data class FavoriteRecipeEntity(
    @PrimaryKey
    val recipeId: String,
    val isFavorite: Boolean = true,
    val bakeCount: Int = 0,
    val lastBakedAt: Long? = null,
    val favoritedAt: Long = System.currentTimeMillis()
)
