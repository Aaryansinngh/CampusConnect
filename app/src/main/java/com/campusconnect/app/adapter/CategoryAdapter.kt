package com.campusconnect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.campusconnect.app.R
import com.campusconnect.app.databinding.ItemCategoryChipBinding

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategorySelected: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedCategory: String = "All"

    inner class CategoryViewHolder(val binding: ItemCategoryChipBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val isSelected = category.equals(selectedCategory, ignoreCase = true)
        
        holder.binding.chipCategory.text = category
        holder.binding.chipCategory.isChecked = isSelected

        if (isSelected) {
            holder.binding.chipCategory.setChipBackgroundColorResource(R.color.primary)
            holder.binding.chipCategory.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.on_primary))
        } else {
            holder.binding.chipCategory.setChipBackgroundColorResource(R.color.chip_bg)
            holder.binding.chipCategory.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_primary))
        }

        holder.binding.chipCategory.setOnClickListener {
            selectedCategory = category
            notifyDataSetChanged()
            onCategorySelected(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}
