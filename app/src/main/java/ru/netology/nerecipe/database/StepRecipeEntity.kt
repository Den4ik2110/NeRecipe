package ru.netology.nerecipe.database

import androidx.room.*

@Entity(tableName = "stepRecipes")
class StepRecipeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idStep")
    val idStep: Long,
    @ColumnInfo(name = "parentTitleRecipe")
    val parentTitleRecipe: String,
    @ColumnInfo(name = "icon")
    val icon: String?,
    @ColumnInfo(name = "description")
    val description: String
)