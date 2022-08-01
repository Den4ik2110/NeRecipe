package ru.netology.nerecipe.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecipe.data.Repository
import ru.netology.nerecipe.data.StepRecipe
import ru.netology.nerecipe.database.AppDb
import ru.netology.nerecipe.util.Constants
import ru.netology.nerecipe.util.SingleLiveEvent
import java.io.File

class RecipeViewModel(application: Application) : AndroidViewModel(application),
    RecipeInteractionListener {

    private val repository = Repository(
        dao = AppDb.getInstance(
            context = application
        ).recipeDao
    )

    val allCategory by repository::allCategory

    private val selectedCategory = MutableLiveData(
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

    fun editCategory(listActivatedCategory: MutableList<String>) {

        val allCategory = allCategory.value
        val missingCategory = mutableListOf<String>()
        allCategory?.forEach {
            if (it !in listActivatedCategory) missingCategory.add(it)
        }
        selectedCategory.value = allCategory?.filter { it in missingCategory }
    }


    private val data: LiveData<List<Recipe>>
        get() = Transformations.switchMap(selectedCategory) { listCategory ->
            val allData by repository::data
            val data = when (listCategory) {
                allCategory.value -> allData
                else -> {
                    Transformations.switchMap(allData) { listData ->
                        val filterData = MutableLiveData<List<Recipe>>()
                        val filterList = listData.filter {
                            val firstWord = it.category.split(" ")[0]
                            firstWord !in listCategory
                        }
                        filterData.value = filterList
                        filterData
                    }
                }
            }
            data
        }


    private val favoriteRequest = MutableLiveData(Constants.FAVORITE_NO)

    private val favoriteData: LiveData<List<Recipe>>
        get() = Transformations.switchMap(favoriteRequest) { request ->
            val previousStepData = data
            val favoriteData = when (request) {
                Constants.FAVORITE_NO -> previousStepData
                else -> {
                    Transformations.switchMap(previousStepData) { listData ->
                        val filterData = MutableLiveData<List<Recipe>>()
                        val filterList = listData.filter { it.isFavorite }
                        filterData.value = filterList
                        filterData
                    }
                }
            }
            favoriteData
        }

    private val searchRequest = MutableLiveData<String?>(null)

    val searchData: LiveData<List<Recipe>>
        get() = Transformations.switchMap(searchRequest) { request ->
            val previousStepData = favoriteData
            val searchData = when (request) {
                null -> previousStepData
                else -> {
                    Transformations.switchMap(previousStepData) { listData ->
                        val filterData = MutableLiveData<List<Recipe>>()
                        val filterList = listData.filter { it.titleRecipe == request }
                        filterData.value = filterList
                        filterData
                    }
                }
            }
            searchData
        }

    private var titleRecipe = MutableLiveData(Constants.TITLE_NONE)

    val listStepRecipe: LiveData<List<StepRecipe>>
        get() = Transformations.switchMap(titleRecipe) { request ->
            val data by repository::stepData
            val stepData = Transformations.switchMap(data) { listData ->
                val filterData = MutableLiveData<List<StepRecipe>>()
                val filterList = listData.filter { it.parentTitleRecipe == request }
                filterData.value = filterList
                filterData
            }
            stepData
        }

    fun editTitleRecipe(title: String) {
        titleRecipe.value = title
    }


    override fun removeRecipe(recipe: Recipe) = repository.recipeRemove(recipe.idRecipe)

    override fun removeStepRecipe(stepRecipe: StepRecipe) =
        repository.deleteStepRecipeId(stepRecipe)

    override fun editRecipe(recipe: Recipe) = repository.recipeEdit(recipe)

    override fun addRecipe(recipe: Recipe) = repository.recipeAdd(recipe)

    fun deleteAll() = repository.deleteAll()

    val navigateToPostContentScreenEvent = SingleLiveEvent<Long>()

    fun addNewRecipe() = navigateToPostContentScreenEvent.call()


    fun editSearchRequest(request: String?) {
        searchRequest.value = request
    }

    fun editFavoriteRequest(request: String) {
        favoriteRequest.value = request
    }

    override fun saveStepRecipe(stepRecipe: StepRecipe) = repository.saveStepRecipe(stepRecipe)

    override fun removeAllParentTitleStepRecipe(title: String) =
        repository.deleteStepRecipeTitle(title)

    private val deleteStep = mutableListOf<StepRecipe>()

    override fun storeDeleteStep(stepRecipe: StepRecipe) {
        deleteStep.add(stepRecipe)
    }

    override fun clearStoreDeleteStep() = deleteStep.clear()

    override fun recoveryStep() {
        deleteStep.forEach {
            saveStepRecipe(it)
        }
    }

    private val addBeforeSavingStep = mutableListOf<StepRecipe>()

    override fun addBeforeSavingStep(stepRecipe: StepRecipe) {
        addBeforeSavingStep.add(stepRecipe)
    }

    override fun clearBeforeSavingStep() = addBeforeSavingStep.clear()

    override fun removeBeforeSavingStep() {
        addBeforeSavingStep.forEach {
            repository.deleteStepDescription(it)
        }
    }

    override fun updateBeforeSavingStep(stepRecipe: StepRecipe) {
        var i = -1
        addBeforeSavingStep.forEachIndexed { index, step ->
            if (step.description == stepRecipe.description) {
                i = index
                addBeforeSavingStep[i] = stepRecipe
            }
        }
    }

    override fun removeStoreDeletePicture() {
        deleteStep.forEach {
            val file = File(it.icon.toString())
            if (file.exists()) {
                file.delete()
                Log.d("MyLog", "file delete")
            }
        }
    }

    override fun removeBeforeSavePicture() {
        addBeforeSavingStep.forEach {
            val file = File(it.icon.toString())
            if (file.exists()) file.delete()
        }
    }

    fun updateTitleStep(titleNew: String, titleOld: String) =
        repository.updateTitleStep(titleNew, titleOld)
}
