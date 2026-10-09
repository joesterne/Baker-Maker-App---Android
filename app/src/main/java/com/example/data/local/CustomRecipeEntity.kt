package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BakingStage
import com.example.data.model.BreadRecipe
import com.example.data.model.CrustShade
import com.example.data.model.FlourCategory
import com.example.data.model.LoafSize
import com.example.data.model.RecipeIngredient
import com.example.data.model.YeastType

@Entity(tableName = "custom_recipes")
data class CustomRecipeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: FlourCategory,
    val primaryFlour: String,
    val specialtyFlours: List<String>,
    val yeastType: YeastType,
    val defaultLoafSize: LoafSize,
    val defaultCrust: CrustShade,
    val hydrationPercent: Int,
    val totalTimeMinutes: Int,
    val ingredients: List<RecipeIngredient>,
    val stages: List<BakingStage>,
    val bakerNotes: List<String>,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): BreadRecipe = BreadRecipe(
        id = id,
        title = title,
        subtitle = subtitle,
        description = description,
        category = category,
        primaryFlour = primaryFlour,
        specialtyFlours = specialtyFlours,
        yeastType = yeastType,
        defaultLoafSize = defaultLoafSize,
        defaultCrust = defaultCrust,
        hydrationPercent = hydrationPercent,
        totalTimeMinutes = totalTimeMinutes,
        ingredients = ingredients,
        stages = stages,
        bakerNotes = bakerNotes,
        isCustom = true
    )

    companion object {
        fun fromDomain(recipe: BreadRecipe): CustomRecipeEntity = CustomRecipeEntity(
            id = recipe.id,
            title = recipe.title,
            subtitle = recipe.subtitle,
            description = recipe.description,
            category = recipe.category,
            primaryFlour = recipe.primaryFlour,
            specialtyFlours = recipe.specialtyFlours,
            yeastType = recipe.yeastType,
            defaultLoafSize = recipe.defaultLoafSize,
            defaultCrust = recipe.defaultCrust,
            hydrationPercent = recipe.hydrationPercent,
            totalTimeMinutes = recipe.totalTimeMinutes,
            ingredients = recipe.ingredients,
            stages = recipe.stages,
            bakerNotes = recipe.bakerNotes
        )
    }
}
