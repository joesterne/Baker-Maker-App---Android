package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.BakingStage
import com.example.data.model.BakingStageType
import com.example.data.model.CrustShade
import com.example.data.model.RecipeCategory
import com.example.data.model.LoafSize
import com.example.data.model.RecipeIngredient
import com.example.data.model.YeastType
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val ingredientListType = Types.newParameterizedType(List::class.java, RecipeIngredient::class.java)
    private val stageListType = Types.newParameterizedType(List::class.java, BakingStage::class.java)
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)

    @TypeConverter
    fun fromIngredientList(value: List<RecipeIngredient>?): String {
        return value?.let { moshi.adapter<List<RecipeIngredient>>(ingredientListType).toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toIngredientList(value: String?): List<RecipeIngredient> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            moshi.adapter<List<RecipeIngredient>>(ingredientListType).fromJson(value) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStageList(value: List<BakingStage>?): String {
        return value?.let { moshi.adapter<List<BakingStage>>(stageListType).toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toStageList(value: String?): List<BakingStage> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            moshi.adapter<List<BakingStage>>(stageListType).fromJson(value) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.let { moshi.adapter<List<String>>(stringListType).toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            moshi.adapter<List<String>>(stringListType).fromJson(value) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromRecipeCategory(value: RecipeCategory): String = value.name

    @TypeConverter
    fun toRecipeCategory(value: String): RecipeCategory = try {
        RecipeCategory.valueOf(value)
    } catch (_: Exception) {
        when (value) {
            "BRIOCHE_SWEET" -> RecipeCategory.SWEET_LOAVES
            "SPELT_ANCIENT", "DARK_RYE", "WHOLE_WHEAT" -> RecipeCategory.WHOLE_GRAIN
            "SPECIALTY" -> RecipeCategory.YEAST_BREAD
            else -> RecipeCategory.ALL
        }
    }

    @TypeConverter
    fun fromYeastType(value: YeastType): String = value.name

    @TypeConverter
    fun toYeastType(value: String): YeastType = try {
        YeastType.valueOf(value)
    } catch (_: Exception) {
        YeastType.INSTANT_YEAST
    }

    @TypeConverter
    fun fromLoafSize(value: LoafSize): String = value.name

    @TypeConverter
    fun toLoafSize(value: String): LoafSize = try {
        LoafSize.valueOf(value)
    } catch (_: Exception) {
        LoafSize.ONE_HALF_LB
    }

    @TypeConverter
    fun fromCrustShade(value: CrustShade): String = value.name

    @TypeConverter
    fun toCrustShade(value: String): CrustShade = try {
        CrustShade.valueOf(value)
    } catch (_: Exception) {
        CrustShade.MEDIUM
    }

    @TypeConverter
    fun fromZojirushiCourse(value: com.example.data.model.ZojirushiMenuCourse): String = value.name

    @TypeConverter
    fun toZojirushiCourse(value: String): com.example.data.model.ZojirushiMenuCourse = try {
        com.example.data.model.ZojirushiMenuCourse.valueOf(value)
    } catch (_: Exception) {
        com.example.data.model.ZojirushiMenuCourse.EUROPEAN
    }
}
