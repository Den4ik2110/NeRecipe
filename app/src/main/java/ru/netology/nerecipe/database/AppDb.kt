package ru.netology.nerecipe.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecipeEntity::class, StepRecipeEntity::class], version = 3)
abstract class AppDb : RoomDatabase() {

    abstract val recipeDao: RecipeDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "data_base_movie.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}