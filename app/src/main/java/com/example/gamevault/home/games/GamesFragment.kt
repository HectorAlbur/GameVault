package com.example.gamevault.home.games

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gamevault.R
import com.example.gamevault.core.ResponseService
import com.example.gamevault.databinding.FragmentGamesBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class GamesFragment : Fragment() {

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<GamesViewModel>()
    private val adapter = GameAdapter { game ->
        val bundle = Bundle().apply { putInt("gameId", game.id) }
        findNavController().navigate(R.id.action_gamesFragment_to_gameDetailFragment, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGamesBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSearch()
        observeState()
        viewModel.loadGames()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvGames.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGames.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener {
            viewModel.searchQuery.value = it.toString().trim()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gamesState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> Log.i("Games", "Cargando...")
                        is ResponseService.Success -> adapter.submitList(state.data)
                        is ResponseService.Error -> Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        null -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}