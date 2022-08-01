package ru.netology.nerecipe.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecipe.data.StepRecipe
import ru.netology.nerecipe.databinding.ActivityCreateAndEditBinding
import ru.netology.nerecipe.util.Constants
import ru.netology.nerecipe.viewmodel.RecipeViewModel
import java.io.*
import java.util.*


class CreateAndEditActivity : AppCompatActivity(), StepAdapter.HideMenuStep, StepAdapter.EditStep {

    private val viewModel by viewModels<RecipeViewModel>()
    private lateinit var binding: ActivityCreateAndEditBinding
    private lateinit var arrayListCategory: List<String>
    private lateinit var localListStepRecipe: MutableLiveData<MutableList<StepRecipe>>
    private lateinit var uriImage: Uri
    private lateinit var adapter: StepAdapter
    private lateinit var keyAction: String
    private lateinit var recipeIntent: Recipe
    private var countStepBegin = 0
    private var stepIdEdit = -1L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAndEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = StepAdapter(this, viewModel, this, this)
        binding.recyclerStep.adapter = adapter

        arrayListCategory = listOf(
            "Европейская кухня",
            "Азиатская кухня",
            "Паназиатская кухня",
            "Восточная кухня",
            "Американская кухня",
            "Русская кухня",
            "Средиземноморская кухня"
        )

        val arrayAdapter = ArrayAdapter(this, R.layout.spinner_item, arrayListCategory)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.textCategoryRecipe.adapter = arrayAdapter

        keyAction = intent.getStringExtra("keyAction").toString()

        if (keyAction == Constants.KEY_EDIT || keyAction == Constants.KEY_BROWSE) {
            recipeIntent = intent.getSerializableExtra("recipe") as Recipe
        }

        when (keyAction) {
            Constants.KEY_EDIT -> {
                viewModel.editTitleRecipe(recipeIntent.titleRecipe)
                binding.buttonEdit.visibility = View.GONE
                binding.groupNewStep.visibility = View.VISIBLE
                binding.updateStepRecipe.visibility = View.GONE
                binding.textAuthor.apply {
                    setText(recipeIntent.nameAuthor)
                    isEnabled = true
                }
                binding.titleRecipe.apply {
                    setText(recipeIntent.titleRecipe)
                    isEnabled = true
                }
                binding.textCategoryRecipe.apply {
                    setSelection(arrayListCategory.indexOf(recipeIntent.category))
                    isEnabled = true
                }
            }
            Constants.KEY_BROWSE -> {
                viewModel.editTitleRecipe(recipeIntent.titleRecipe)
                binding.buttonSave.visibility = View.GONE
                binding.groupNewStep.visibility = View.GONE
                binding.updateStepRecipe.visibility = View.GONE
                binding.textAuthor.apply {
                    setText(recipeIntent.nameAuthor)
                    isEnabled = false
                }
                binding.titleRecipe.apply {
                    setText(recipeIntent.titleRecipe)
                    isEnabled = false
                }
                binding.textCategoryRecipe.apply {
                    setSelection(arrayListCategory.indexOf(recipeIntent.category))
                    isEnabled = false
                }
            }
            Constants.KEY_CREATE -> {
                binding.buttonEdit.visibility = View.GONE
                binding.groupNewStep.visibility = View.VISIBLE
                binding.updateStepRecipe.visibility = View.GONE
                binding.textAuthor.isEnabled = true
                binding.titleRecipe.isEnabled = true
                binding.textCategoryRecipe.isEnabled = true
            }
        }

        localListStepRecipe = MutableLiveData(mutableListOf())

        viewModel.listStepRecipe.observe(this) {
            adapter.submitList(it)
            setCountStepBegin(it.size)
        }

        binding.buttonEdit.setOnClickListener {
            keyAction = Constants.KEY_EDIT
            binding.buttonEdit.visibility = View.GONE
            binding.buttonSave.visibility = View.VISIBLE
            binding.groupNewStep.visibility = View.VISIBLE
            binding.textAuthor.apply {
                setText(recipeIntent.nameAuthor)
                isEnabled = true
            }
            binding.titleRecipe.apply {
                setText(recipeIntent.titleRecipe)
                isEnabled = true
            }
            binding.textCategoryRecipe.apply {
                setSelection(arrayListCategory.indexOf(recipeIntent.category))
                isEnabled = true
            }
        }


        binding.saveStepRecipe.setOnClickListener {
            if (binding.titleRecipe.text.isBlank() || binding.textStep.text.isBlank()) Toast.makeText(
                this,
                "Шаг не сохранен! Сперва введите название рецепта",
                Toast.LENGTH_SHORT
            ).show() else {
                val step = StepRecipe(
                    0,
                    binding.titleRecipe.text.toString(),
                    binding.textStep.text.toString(),
                    if (binding.addImage.drawable == null) null else savePicture(binding.addImage)
                )
                viewModel.editTitleRecipe(binding.titleRecipe.text.toString())
                viewModel.saveStepRecipe(step)
                viewModel.addBeforeSavingStep(step)
            }
            binding.apply {
                textStep.text.clear()
                addImage.setImageDrawable(null)
            }
        }

        binding.updateStepRecipe.setOnClickListener {
            val step = StepRecipe(
                stepIdEdit,
                binding.titleRecipe.text.toString(),
                binding.textStep.text.toString(),
                if (binding.addImage.drawable == null) null else savePicture(binding.addImage)
            )
            viewModel.saveStepRecipe(step)
            viewModel.updateBeforeSavingStep(step)
            binding.apply {
                textStep.text.clear()
                addImage.setImageDrawable(null)
                textActionStep.text = "Создание нового шага"
            }
        }

        binding.buttonSave.setOnClickListener {
            if (binding.textAuthor.text.isNotBlank() && binding.titleRecipe.text.isNotBlank()) {
                if (adapter.itemCount != 0) {
                    viewModel.removeStoreDeletePicture()
                    viewModel.clearStoreDeleteStep()
                    viewModel.clearBeforeSavingStep()
                    when (keyAction) {
                        Constants.KEY_EDIT -> {
                            viewModel.editRecipe(
                                Recipe(
                                    binding.textAuthor.text.toString(),
                                    binding.titleRecipe.text.toString(),
                                    binding.textCategoryRecipe.selectedItem.toString(),
                                    idRecipe = recipeIntent.idRecipe
                                )
                            )
                            viewModel.updateTitleStep(
                                binding.titleRecipe.text.toString(),
                                recipeIntent.titleRecipe
                            )
                        }
                        Constants.KEY_CREATE -> {
                            viewModel.addRecipe(
                                Recipe(
                                    binding.textAuthor.text.toString(),
                                    binding.titleRecipe.text.toString(),
                                    binding.textCategoryRecipe.selectedItem.toString()
                                )
                            )
                        }
                    }
                    finish()
                } else Toast.makeText(
                    this,
                    "Для сохранения рецепта создайте хотя бы один шаг",
                    Toast.LENGTH_SHORT
                ).show()
            } else Toast.makeText(this, "Необходимо заполнить все поля", Toast.LENGTH_SHORT).show()
        }

        binding.addImage.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_PICK
                type = "image/*"
            }
            val shareIntent = Intent.createChooser(intent, getString(android.R.string.search_go))
            startActivityForResult(shareIntent, Constants.INTENT_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == RESULT_OK && requestCode == Constants.INTENT_PHOTO) {
            val selectedImage: Uri? = data?.data
            if (selectedImage != null) {
                uriImage = selectedImage
            }
            binding.addImage.setImageURI(uriImage)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun hideMenu(menu: Button) {
        when (keyAction) {
            Constants.KEY_BROWSE -> menu.visibility = View.GONE
            else -> menu.visibility = View.VISIBLE
        }
    }

    override fun stepCreate(step: StepRecipe) {
        stepIdEdit = step.idStep
        binding.apply {
            textActionStep.text = "Редактирование шага"
            textStep.setText(step.description)
            addImage.setImageURI(Uri.parse(step.icon.toString()))
            updateStepRecipe.visibility = View.VISIBLE
            saveStepRecipe.visibility = View.GONE
        }
    }

    private fun setCountStepBegin(count: Int) {
        if (countStepBegin == 0) countStepBegin = count
    }

    override fun onBackPressed() {
        when (keyAction) {
            Constants.KEY_EDIT -> {
                if (adapter.itemCount == 0) {
                    val alertDialog = ExitRecipeEditStepNullFragment(viewModel, recipeIntent, this)
                    alertDialog.show(supportFragmentManager, "keyOne")
                } else {
                    if (countStepBegin == adapter.itemCount &&
                        recipeIntent.titleRecipe == binding.titleRecipe.text.toString() &&
                        recipeIntent.nameAuthor == binding.textAuthor.text.toString() &&
                        recipeIntent.category == binding.textCategoryRecipe.selectedItem.toString()
                    ) {
                        super.onBackPressed()
                    } else {
                        val recipeUpdate = Recipe(
                            binding.textAuthor.text.toString(),
                            binding.titleRecipe.text.toString(),
                            binding.textCategoryRecipe.selectedItem.toString()
                        )
                        val alertDialog = ExitRecipeEditFragment(viewModel, recipeUpdate, this)
                        alertDialog.show(supportFragmentManager, "keyOne")
                    }
                }
            }
            Constants.KEY_CREATE -> {
                if (binding.textAuthor.text.isBlank() || binding.titleRecipe.text.isBlank() || adapter.itemCount == 0) {
                    super.onBackPressed()
                } else {
                    val recipeNew = Recipe(
                        binding.textAuthor.text.toString(),
                        binding.titleRecipe.text.toString(),
                        binding.textCategoryRecipe.selectedItem.toString()
                    )
                    val alertDialog = ExitRecipeCreateFragment(
                        viewModel,
                        recipeNew,
                        binding.titleRecipe.text.toString(),
                        this
                    )
                    alertDialog.show(supportFragmentManager, "keyOne")
                }
            }
            Constants.KEY_BROWSE -> super.onBackPressed()
        }
    }

    private fun savePicture(iv: ImageView): String? {
        val fileOut: OutputStream?
        val cw = ContextWrapper(applicationContext)
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val file: File
        try {
            file = File(directory, UUID.randomUUID().toString() + ".jpg")
            fileOut = FileOutputStream(file)
            iv.drawable.toBitmap().compress(
                Bitmap.CompressFormat.JPEG,
                85,
                fileOut
            )
            fileOut.flush()
            fileOut.close()
        } catch (e: Exception) {
            return e.message
        }
        return file.absolutePath
    }


}