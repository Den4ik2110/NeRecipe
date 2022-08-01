package ru.netology.nerecipe.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.ActivityMainBinding
import ru.netology.nerecipe.util.Constants
import ru.netology.nerecipe.util.SimpleItemTouchHelperCallback
import ru.netology.nerecipe.viewmodel.RecipeViewModel


class MainActivity : AppCompatActivity(), RecipeAdapter.OnStartDragListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<RecipeViewModel>()
    private lateinit var behaviorSheetBehavior: BottomSheetBehavior<View>
    private lateinit var adapter: RecipeAdapter
    private lateinit var touchHelper: ItemTouchHelper
    private lateinit var listActivatedCategory: MutableList<String>
    private var arrayForAdapterSearch: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.bottomAppBar)
        behaviorSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        behaviorSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        behaviorSheetBehavior.peekHeight = 0
        adapter = RecipeAdapter(this, viewModel)
        binding.recycleRecipe.adapter = adapter

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        val searchAutoComplete = findViewById<AutoCompleteTextView>(
            resources.getIdentifier("android:id/search_src_text", null, null)
        )

        searchAutoComplete.apply {
            threshold = 2
            setOnItemClickListener { _, view, _, _ ->
                val text = view.findViewById<TextView>(R.id.suggetion_text).text
                binding.searchView.setQuery(text, false)
            }
        }

        val callback = SimpleItemTouchHelperCallback(adapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.recycleRecipe)

        viewModel.searchData.observe(this) { recipes ->
            arrayForAdapterSearch.clear()
            recipes.forEach {
                arrayForAdapterSearch.add(it.titleRecipe)
            }
            val arrayAdapter = ArrayAdapter(this, R.layout.suggetion, arrayForAdapterSearch)
            searchAutoComplete.setAdapter(arrayAdapter)

            binding.plugIcon.visibility = if (recipes.isNotEmpty()) View.GONE else View.VISIBLE
            adapter.submitList(recipes)
        }

        viewModel.allCategory.observe(this) { category ->
            listActivatedCategory = category.toMutableList()
        }


        viewModel.navigateToPostContentScreenEvent.observe(this)
        {
            val intent = Intent(this, CreateAndEditActivity::class.java)
            intent.putExtra("keyAction", Constants.KEY_CREATE)
            startActivity(intent)
        }

        binding.fab.setOnClickListener {
            viewModel.addNewRecipe()
        }

        binding.buttonHideSheet.setOnClickListener {
            behaviorSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.chipAmerican.setOnCheckedChangeListener { chip, isChecked ->
            filter(isChecked, chip)
        }
        binding.chipEuropa.setOnCheckedChangeListener { chip, isChecked ->
            filter(isChecked, chip)
        }
        binding.chipEast.setOnCheckedChangeListener { chip, isChecked ->
            filter(isChecked, chip)
        }
        binding.chipPanAsia.setOnCheckedChangeListener { chip, isChecked ->
            filter(isChecked, chip)
        }
        binding.chipAsia.setOnCheckedChangeListener { chip, isChecked ->
            filter(isChecked, chip)
        }
        binding.chipRussian.setOnCheckedChangeListener { chip, isChecked ->
            filter(isChecked, chip)
        }
        binding.chipMiddleSea.setOnCheckedChangeListener { chip, isChecked ->
            filter(isChecked, chip)
        }

        val searchCloseBtnId = binding.searchView.context.resources.getIdentifier(
            "android:id/search_close_btn",
            null,
            null
        )

        val closeButton = binding.searchView.findViewById<ImageView>(searchCloseBtnId)

        closeButton.setOnClickListener {
            viewModel.editSearchRequest(null)
            binding.searchView.apply {
                setQuery("", false)
                clearFocus()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_buttom_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> behaviorSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            R.id.button_favorite -> {
                viewModel.editFavoriteRequest(Constants.FAVORITE_YES)
                binding.bottomAppBar.menu.findItem(R.id.button_favorite)
                    .setIcon(R.drawable.ic_favorite_on)
                binding.bottomAppBar.menu.findItem(R.id.button_all_recipes)
                    .setIcon(R.drawable.ic_all_recipe_off)
            }
            R.id.button_all_recipes -> {
                viewModel.editFavoriteRequest(Constants.FAVORITE_NO)
                binding.bottomAppBar.menu.findItem(R.id.button_favorite)
                    .setIcon(R.drawable.ic_favorite_off)
                binding.bottomAppBar.menu.findItem(R.id.button_all_recipes)
                    .setIcon(R.drawable.ic_all_recipe_on)
            }
        }
        return true
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)
    }

    private fun filter(isChecked: Boolean, chip: CompoundButton) {
        if (!isChecked) {
            if (listActivatedCategory.size > 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    listActivatedCategory.removeIf { it == chip.text.toString() }
                }
            } else chip.isChecked = true
        } else listActivatedCategory.add(chip.text.toString())
        viewModel.editCategory(listActivatedCategory)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            if (Intent.ACTION_SEARCH == intent.action) {
                intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                    viewModel.editSearchRequest(query)
                }
            }
        }
    }
}