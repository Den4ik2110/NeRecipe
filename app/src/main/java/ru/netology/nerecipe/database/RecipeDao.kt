package ru.netology.nerecipe.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecipeDao {

    @Query("DELETE FROM recipes WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM stepRecipes WHERE idStep = :id")
    fun deleteStepId(id: Long)

    @Query("DELETE FROM stepRecipes WHERE parentTitleRecipe = :title")
    fun deleteStepTitle(title: String)

    @Query("DELETE FROM stepRecipes WHERE description = :description AND parentTitleRecipe = :title")
    fun deleteStepDescription(description: String, title: String)

    @Insert
    fun addRecipe(recipe: RecipeEntity)

    @Update
    fun updateRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes")
    fun deleteAll()

    @Query("SELECT * FROM recipes")
    fun getFromDataBase(): LiveData<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStepRecipe(stepRecipe: StepRecipeEntity)

    @Query("SELECT * FROM stepRecipes")
    fun getListStepRecipe():  LiveData<List<StepRecipeEntity>>

    @Query("UPDATE stepRecipes SET parentTitleRecipe = :titleNew WHERE parentTitleRecipe = :titleOld")
    fun updateTitleStep(titleNew: String, titleOld: String)
}