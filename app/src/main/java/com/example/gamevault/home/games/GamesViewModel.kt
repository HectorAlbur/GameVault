package com.example.gamevault.home.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.Game
import com.example.gamevault.core.network.GameService
import com.example.gamevault.core.repositories.GameRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class GamesViewModel(
    private val service: GameService = GameRepository()
) : ViewModel() {

    private val _gamesState = MutableStateFlow<ResponseService<List<Game>>?>(null)
    val gamesState: StateFlow<ResponseService<List<Game>>?> = _gamesState.asStateFlow()

    val searchQuery = MutableStateFlow("")

    init {
        loadGames()
        observeSearch()
    }

    fun loadGames() {
        viewModelScope.launch {
            _gamesState.value = ResponseService.Loading
            _gamesState.value = service.getGames()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery.debounce(400).collect { query ->
                if (query.isBlank()) {
                    loadGames()
                } else {
                    _gamesState.value = ResponseService.Loading
                    _gamesState.value = service.searchGames(query)
                }
            }
        }
    }
}