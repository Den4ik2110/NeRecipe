package ru.netology.nerecipe.viewmodel

import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecipe.data.StepRecipe

interface RecipeInteractionListener {

    fun removeRecipe(recipe: Recipe)

    fun addRecipe(recipe: Recipe)

    fun removeStepRecipe(stepRecipe: StepRecipe)

    fun editRecipe(recipe: Recipe)

    fun saveStepRecipe(stepRecipe: StepRecipe)

    fun removeAllParentTitleStepRecipe(title: String)

    fun storeDeleteStep(stepRecipe: StepRecipe)

    fun clearStoreDeleteStep()

    fun recoveryStep()

    fun addBeforeSavingStep(stepRecipe: StepRecipe)

    fun clearBeforeSavingStep()

    fun removeBeforeSavingStep()

    fun updateBeforeSavingStep(stepRecipe: StepRecipe)

    fun removeStoreDeletePicture()

    fun removeBeforeSavePicture()
}
