package ru.netology.nerecipe.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.database.RecipeDao
import ru.netology.nerecipe.database.toEntity
import ru.netology.nerecipe.database.toRecipe
import ru.netology.nerecipe.database.toStepRecipe

class Repository(
    private val dao: RecipeDao
) {
    val data: LiveData<List<Recipe>>
        get() = dao.getFromDataBase().map { entities ->
            entities.map { it.toRecipe() }
        }

    val stepData: LiveData<List<StepRecipe>>
        get() = dao.getListStepRecipe().map { entities ->
            entities.map { it.toStepRecipe() }
        }


    val allCategory = MutableLiveData(
        listOf(
            "Европейская",
            "Азиатская",
            "Паназиатская",
            "Восточная",
            "Американская",
            "Русская",
            "Средиземноморская"
        )
    )

    fun recipeRemove(recipeId: Long) {
        dao.delete(recipeId)
    }

    fun recipeAdd(recipe: Recipe) {
        dao.addRecipe(recipe.toEntity())
    }

    fun recipeEdit(recipe: Recipe) {
        Log.d("MyLog", recipe.toString())
        dao.updateRecipe(recipe.toEntity())
    }

    fun deleteAll() {
        dao.deleteAll()
    }

    fun saveStepRecipe(stepRecipe: StepRecipe) {
        dao.saveStepRecipe(stepRecipe.toEntity())
    }

    fun deleteStepRecipeId(stepRecipe: StepRecipe){
        dao.deleteStepId(stepRecipe.idStep)
    }

    fun deleteStepRecipeTitle(title: String){
        dao.deleteStepTitle(title)
    }

    fun updateTitleStep(titleNew: String, titleOld: String) {
        dao.updateTitleStep(titleNew, titleOld)
    }

    fun deleteStepDescription(stepRecipe: StepRecipe) {
        dao.deleteStepDescription(stepRecipe.description, stepRecipe.parentTitleRecipe)
    }
}
