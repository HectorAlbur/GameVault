package com.example.gamevault.home.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamevault.databinding.ItemWishlistBinding
import com.example.gamevault.home.wishlist.model.FavoriteGame

class WishlistAdapter(
    private val onItemClick: (FavoriteGame) -> Unit = {},
    private val onRemoveClick: (FavoriteGame) -> Unit = {}
) : ListAdapter<FavoriteGame, WishlistAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val binding: ItemWishlistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FavoriteGame) {
            binding.tvTitle.text = item.name
            binding.tvRating.text = "★ ${String.format("%.1f", item.rating)}"
            binding.tvGenres.text = item.genresText
            Glide.with(binding.imgCover)
                .load(item.backgroundImage)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(binding.imgCover)
            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnRemove.setOnClickListener { onRemoveClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<FavoriteGame>() {
            override fun areItemsTheSame(oldItem: FavoriteGame, newItem: FavoriteGame) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: FavoriteGame, newItem: FavoriteGame) =
                oldItem == newItem
        }
    }
}