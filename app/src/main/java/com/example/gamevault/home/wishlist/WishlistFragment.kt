package com.example.gamevault.home.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamevault.R
import com.example.gamevault.core.model.Game
import com.example.gamevault.databinding.FragmentWishlistBinding
import com.example.gamevault.home.wishlist.model.FavoriteGame
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<WishlistViewModel>()
    private lateinit var adapter: WishlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeState()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    private fun setupRecyclerView() {
        adapter = WishlistAdapter(
            onItemClick = { favorite -> navigateToDetail(favorite) },
            onRemoveClick = { favorite -> viewModel.removeFavorite(favorite) }
        )
        binding.rvWishlist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWishlist.adapter = adapter
    }

    private fun navigateToDetail(favorite: FavoriteGame) {
        val bundle = Bundle().apply { putInt("gameId", favorite.id) }
        findNavController().navigate(
            R.id.action_wishlistFragment_to_gameDetailFragment, bundle
        )
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.games.collect { games ->
                    adapter.submitList(games)
                    binding.rvWishlist.visibility = if (games.isEmpty()) View.GONE else View.VISIBLE
                    binding.layoutEmpty.visibility = if (games.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}