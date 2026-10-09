package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [RecipeEntity::class, CustomRecipeEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun customRecipeDao(): CustomRecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "crustcraft_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate Room database with initial sourdough, specialty, and official manual recipes
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedInitialRecipes(database.recipeDao(), database.customRecipeDao())
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    // Ensure latest preloaded and official manual recipes are seeded
                                    seedInitialRecipes(database.recipeDao(), database.customRecipeDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedInitialRecipes(dao: RecipeDao, customDao: CustomRecipeDao? = null) {
            val entities = PreloadedRecipes.RECIPES.map { RecipeEntity.fromDomain(it) }
            dao.insertAll(entities)

            // Seed initial custom recipes into the custom_recipes Room table
            customDao?.let { cDao ->
                if (cDao.getCustomRecipeCount() == 0) {
                    cDao.insertAllCustomRecipes(PreloadedCustomRecipes.INITIAL_CUSTOM_RECIPES)
                    // Also mirror into main recipes table so they are immediately searchable
                    val customEntities = PreloadedCustomRecipes.INITIAL_CUSTOM_RECIPES.map {
                        RecipeEntity.fromDomain(it.toDomain())
                    }
                    dao.insertAll(customEntities)
                }
            }
        }
    }
}
