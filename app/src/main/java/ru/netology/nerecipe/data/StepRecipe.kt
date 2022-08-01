package ru.netology.nerecipe.data


data class StepRecipe(
    val idStep: Long,
    val parentTitleRecipe: String,
    val description: String,
    val icon: String?
)