package com.example.gamevault.home.games

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.gamevault.R
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.Game
import com.example.gamevault.databinding.FragmentGameDetailBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class GameDetailFragment : Fragment() {

    private var _binding: FragmentGameDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<GameDetailViewModel>()
    private var currentGame: Game? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recibe el ID del juego
        val gameId = requireArguments().getInt("gameId")

        // llama a GET /games/{id}
        viewModel.loadGame(gameId)

        observeGameState()
        observeFavoriteState()
        setupClickListeners()
    }

    private fun observeGameState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is ResponseService.Success -> {
                            binding.progressBar.visibility = View.GONE
                            currentGame = state.data
                            bindGameInfo(state.data)
                        }
                        is ResponseService.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    private fun bindGameInfo(game: Game) {
        Glide.with(binding.ivCover)
            .load(game.backgroundImage)
            .centerCrop()
            .placeholder(android.R.color.darker_gray)
            .into(binding.ivCover)

        binding.tvTitle.text = game.name
        binding.tvRating.text = "★ ${String.format("%.1f", game.rating)} (${game.ratingsCount} votos)"
        binding.tvReleased.text = "Lanzado: ${game.released ?: "Fecha no disponible"}"
        binding.tvGenres.text = game.genres.joinToString(" · ") { it.name }.ifEmpty { "N/A" }
        binding.tvPlatforms.text = game.platforms
            ?.joinToString(" · ") { it.platform.name } ?: "N/A"
        binding.tvMetacritic.text = game.metacritic?.toString() ?: "N/A"
        binding.tvDeveloper.text = game.developers
            ?.firstOrNull()?.name ?: "N/A"

        // la descripción viene en HTML, la convertimos a texto
        val descRaw = game.description ?: "Sin descripción disponible"
        binding.tvDescription.text = Html.fromHtml(descRaw, Html.FROM_HTML_MODE_LEGACY)
            .toString().trim()
    }

    private fun observeFavoriteState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFavorite.collect { isFav ->
                    binding.btnFavorite.setImageResource(
                        if (isFav) R.drawable.ic_heart_filled
                        else R.drawable.ic_favorite
                    )
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener { findNavController().navigateUp() }
        binding.btnFavorite.setOnClickListener {
            currentGame?.let { viewModel.toggleFavorite(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}