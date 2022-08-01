package ru.netology.nerecipe.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.AndroidViewModel
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecipe.viewmodel.RecipeInteractionListener
import ru.netology.nerecipe.viewmodel.RecipeViewModel

class ExitRecipeEditStepNullFragment(
    private val listener: RecipeInteractionListener,
    private val recipe: Recipe,
    private val activityCreate: CreateAndEditActivity

) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialog_exit_recipe_edit_step_null)
                .setPositiveButton(
                    R.string.dialog_yes
                ) { _, _ ->
                    listener.apply {
                        removeRecipe(recipe)
                        removeBeforeSavePicture()
                        removeStoreDeletePicture()
                        clearBeforeSavingStep()
                        clearStoreDeleteStep()
                    }
                    activityCreate.finish()
                }
                .setNegativeButton(
                    R.string.dialog_no
                ) { _, _ ->
                    listener.recoveryStep()
                    listener.clearStoreDeleteStep()
                }
            setStyle(STYLE_NO_TITLE, 0)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class ExitRecipeEditFragment(
    private val listener: RecipeInteractionListener,
    private val recipe: Recipe,
    private val activityCreate: CreateAndEditActivity
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialog_exit_recipe_edit)
                .setPositiveButton(
                    R.string.dialog_yes
                ) { _, _ ->
                    listener.apply {
                        editRecipe(recipe)
                        removeStoreDeletePicture()
                        clearStoreDeleteStep()
                        clearBeforeSavingStep()
                    }
                    activityCreate.finish()
                }
                .setNegativeButton(
                    R.string.dialog_no
                ) { _, _ ->
                    listener.apply {
                        recoveryStep()
                        removeBeforeSavingStep()
                        clearBeforeSavingStep()
                        clearStoreDeleteStep()
                    }
                    activityCreate.finish()
                }
            setStyle(STYLE_NO_TITLE, 0)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

class ExitRecipeCreateFragment(
    private val listener: RecipeInteractionListener,
    private val recipe: Recipe,
    private val title: String,
    private val activityCreate: CreateAndEditActivity

) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialog_exit_recipe_create)
                .setPositiveButton(
                    R.string.dialog_yes
                ) { _, _ ->
                    listener.apply {
                        addRecipe(recipe)
                        clearStoreDeleteStep()
                    }
                    activityCreate.finish()
                }
                .setNegativeButton(
                    R.string.dialog_no
                ) { _, _ ->
                    listener.apply {
                        removeAllParentTitleStepRecipe(title)
                        removeBeforeSavePicture()
                        clearStoreDeleteStep()
                    }
                    activityCreate.finish()
                }
            setStyle(STYLE_NO_TITLE, 0)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
