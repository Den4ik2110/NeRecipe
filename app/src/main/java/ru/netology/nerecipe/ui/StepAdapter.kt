package ru.netology.nerecipe.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.StepRecipe
import ru.netology.nerecipe.databinding.OneStepBinding
import ru.netology.nerecipe.util.ItemTouchHelperAdapter
import ru.netology.nerecipe.viewmodel.RecipeInteractionListener

class StepAdapter(
    private val context: Context,
    private val listener: RecipeInteractionListener,
    private val hideMenu: HideMenuStep,
    private val editStep: EditStep
) : ListAdapter<StepRecipe, StepAdapter.MovieHolder>(DiffCallback), ItemTouchHelperAdapter {


    class MovieHolder(
        private val binding: OneStepBinding,
        private val context: Context,
        private val listener: RecipeInteractionListener,
        private val hideMenu: HideMenuStep,
        private val editStep: EditStep
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var step: StepRecipe

        init {
            binding.buttonMenuOneStep.setOnClickListener {
                popupMenu.show()
            }
        }

        private val popupMenu by lazy {
            PopupMenu(itemView.context, binding.buttonMenuOneStep).apply {
                inflate(R.menu.menu_one_step)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit_step -> {
                            editStep.stepCreate(step)
                            true
                        }
                        R.id.menu_delete_step -> {
                            listener.storeDeleteStep(step)
                            listener.removeStepRecipe(step)
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            }
        }

        fun bind(step: StepRecipe, holder: MovieHolder) {
            this.step = step
            hideMenu.hideMenu(binding.buttonMenuOneStep)
            with(binding) {
                if (step.icon != null) imageStep.visibility = View.VISIBLE else imageStep.visibility = View.GONE
                imageStep.setImageBitmap(BitmapFactory.decodeFile(step.icon))
                textDescriptionStep.text = step.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OneStepBinding.inflate(inflater, parent, false)
        return MovieHolder(binding, context, listener, hideMenu, editStep)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        holder.bind(getItem(position), holder)
    }

    private object DiffCallback : DiffUtil.ItemCallback<StepRecipe>() {

        override fun areItemsTheSame(oldItem: StepRecipe, newItem: StepRecipe): Boolean =
            oldItem.idStep == newItem.idStep

        override fun areContentsTheSame(oldItem: StepRecipe, newItem: StepRecipe): Boolean =
            newItem == oldItem
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    interface HideMenuStep {
        fun hideMenu(menu: Button)
    }

    interface EditStep {
        fun stepCreate(step: StepRecipe)
    }
}