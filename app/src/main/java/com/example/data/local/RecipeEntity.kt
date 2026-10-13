package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.BakingStage
import com.example.data.model.BreadRecipe
import com.example.data.model.CrustShade
import com.example.data.model.RecipeCategory
import com.example.data.model.LoafSize
import com.example.data.model.RecipeIngredient
import com.example.data.model.YeastType

@Entity(
    tableName = "recipes",
    indices = [
        Index("category"),
        Index("isSourdough")
    ]
)
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: RecipeCategory,
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
    val isCustom: Boolean = false,
    val isSourdough: Boolean = false,
    val zojirushiRecommendedCourse: com.example.data.model.ZojirushiMenuCourse = com.example.data.model.ZojirushiMenuCourse.EUROPEAN,
    val createdAt: Long = System.currentTimeMillis(),
    val photoUri: String? = null
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
        isCustom = isCustom,
        zojirushiRecommendedCourse = zojirushiRecommendedCourse,
        photoUri = photoUri
    )

    companion object {
        fun fromDomain(recipe: BreadRecipe): RecipeEntity = RecipeEntity(
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
            bakerNotes = recipe.bakerNotes,
            isCustom = recipe.isCustom,
            isSourdough = recipe.category == RecipeCategory.SOURDOUGH || recipe.yeastType == YeastType.SOURDOUGH_STARTER,
            zojirushiRecommendedCourse = recipe.zojirushiRecommendedCourse,
            photoUri = recipe.photoUri
        )
    }
}
