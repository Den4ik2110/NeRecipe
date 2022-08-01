package ru.netology.nerecipe.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.Recipe
import ru.netology.nerecipe.databinding.OneRecipeBinding
import ru.netology.nerecipe.util.Constants
import ru.netology.nerecipe.util.ItemTouchHelperAdapter
import ru.netology.nerecipe.viewmodel.RecipeInteractionListener

class RecipeAdapter(
    private val context: Context,
    private val listener: RecipeInteractionListener
) : ListAdapter<Recipe, RecipeAdapter.MovieHolder>(DiffCallback), ItemTouchHelperAdapter {


    class MovieHolder(
        private val binding: OneRecipeBinding,
        private val context: Context,
        private val listener: RecipeInteractionListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipe: Recipe

        init {
            binding.buttonMenuRecipe.setOnClickListener {
                popupMenu.show()
            }
            binding.switchFavorite.setOnCheckedChangeListener { _, isChecked ->
                recipe.isFavorite = isChecked
                listener.editRecipe(recipe)
            }
            binding.root.setOnClickListener {
                context.apply {
                    val intent = Intent(this, CreateAndEditActivity::class.java)
                    intent.putExtra("recipe", recipe)
                        .putExtra("keyAction", Constants.KEY_BROWSE)
                    startActivity(intent)
                }
            }
        }

        private val popupMenu by lazy {
            PopupMenu(itemView.context, binding.buttonMenuRecipe).apply {
                inflate(R.menu.popup_menu)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            context.apply {
                                val intent = Intent(this, CreateAndEditActivity::class.java)
                                intent.putExtra("recipe", recipe)
                                    .putExtra("keyAction", Constants.KEY_EDIT)
                                startActivity(intent)
                            }
                            true
                        }
                        R.id.menu_delete -> {
                            listener.removeRecipe(recipe)
                            listener.removeAllParentTitleStepRecipe(recipe.titleRecipe)
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            }
        }

        fun bind(recipe: Recipe, holder: MovieHolder) {
            this.recipe = recipe

            with(binding) {
                textNameAuthor.text = recipe.nameAuthor
                textTitleRecipe.text = recipe.titleRecipe
                textCategory.text = recipe.category
                switchFavorite.isChecked = recipe.isFavorite

                /*cardMovie.setOnClickListener {
                    bindingMainActivity.searchView.clearFocus()
                }*/
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OneRecipeBinding.inflate(inflater, parent, false)
        return MovieHolder(binding, context, listener)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        holder.bind(getItem(position), holder)
    }

    private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {

        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            oldItem.idRecipe == newItem.idRecipe

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            newItem == oldItem
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }
}