package com.example.weather360.ui.favorite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather360.databinding.FavoriteItemBinding
import com.example.weather360.model.FavoriteLocation

class FavoriteAdapter(private val context: Context, val onClick: (FavoriteLocation) -> Unit, val onDelete: (FavoriteLocation) -> Unit) :
    ListAdapter<FavoriteLocation, FavoriteAdapter.FavoriteViewHolder>(ProductDiffUtil()) {

    private lateinit var binding: FavoriteItemBinding

    class FavoriteViewHolder(var binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FavoriteItemBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.binding.apply {
            tvFavoriteName.text = currentItem.name
        }

        holder.binding.favoriteLayout.setOnClickListener {
            onClick(currentItem)
        }

        holder.binding.btnFavoriteRemove.setOnClickListener{
            onDelete(currentItem)
        }
    }
}

class ProductDiffUtil : DiffUtil.ItemCallback<FavoriteLocation>() {
    override fun areItemsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {
        return oldItem == newItem
    }

}