package com.example.gamevault.home.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamevault.core.model.Game
import com.example.gamevault.databinding.ItemGameBinding

class GameAdapter(
    private val onItemClick: (Game) -> Unit = {}
) : ListAdapter<Game, GameAdapter.GameViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GameViewHolder(
        private val binding: ItemGameBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) {
            Glide.with(binding.imgCover)
                .load(game.backgroundImage)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(binding.imgCover)

            binding.chipRating.text = "★ ${String.format("%.1f", game.rating)}"
            binding.tvTitle.text = game.name
            binding.tvGenres.text = game.genres.take(2).joinToString(" · ") { it.name }
            binding.tvPlatforms.text = game.platforms
                ?.take(3)
                ?.joinToString(" · ") { it.platform.name } ?: ""

            binding.root.setOnClickListener { onItemClick(game) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldItem: Game, newItem: Game) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Game, newItem: Game) =
                oldItem == newItem
        }
    }
}