package ru.netology.nerecipe.data

import java.io.Serializable

data class Recipe(
    val nameAuthor: String,
    val titleRecipe: String,
    val category: String,
    //val stepRecipes: MutableList<StepRecipe>,
    var isFavorite: Boolean = false,
    val idRecipe: Long = 0
) : Serializable