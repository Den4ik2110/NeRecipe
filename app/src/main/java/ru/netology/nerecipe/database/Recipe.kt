package ru.netology.nerecipe.database

import android.net.Uri
import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecipe.data.StepRecipe

internal fun RecipeEntity.toRecipe() = Recipe(
    idRecipe = id,
    nameAuthor = nameAuthor,
    titleRecipe = titleRecipe,
    category = category,
    isFavorite = isFavorite
)

internal fun Recipe.toEntity() = RecipeEntity(
    id = idRecipe,
    nameAuthor = nameAuthor,
    titleRecipe = titleRecipe,
    category = category,
    isFavorite = isFavorite
)

internal fun StepRecipe.toEntity() = StepRecipeEntity(
    idStep = idStep,
    parentTitleRecipe = parentTitleRecipe,
    description = description,
    icon = icon
)

internal fun StepRecipeEntity.toStepRecipe() = StepRecipe(
    idStep = idStep,
    parentTitleRecipe = parentTitleRecipe,
    description = description,
    icon = icon
)