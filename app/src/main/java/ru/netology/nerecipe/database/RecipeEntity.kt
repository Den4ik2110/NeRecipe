package ru.netology.nerecipe.database

import androidx.room.*

@Entity(tableName = "recipes")
class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "author")
    val nameAuthor: String,
    @ColumnInfo(name = "title")
    val titleRecipe: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false
)