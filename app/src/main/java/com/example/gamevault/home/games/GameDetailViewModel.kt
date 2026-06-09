package com.example.gamevault.home.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.Game
import com.example.gamevault.core.network.ApiClient
import com.example.gamevault.home.wishlist.model.FavoriteGame
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GameDetailViewModel : ViewModel() {

    private val api = ApiClient.api
    private val db = FirebaseFirestore.getInstance()
    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _gameState = MutableStateFlow<ResponseService<Game>?>(null)
    val gameState: StateFlow<ResponseService<Game>?> = _gameState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Llama a GET /games/{id} con el ID del juego
    fun loadGame(gameId: Int) {
        viewModelScope.launch {
            _gameState.value = ResponseService.Loading
            try {
                val game = api.getGameDetail(gameId)
                _gameState.value = ResponseService.Success(game)
                checkFavorite(gameId)
            } catch (e: Exception) {
                _gameState.value = ResponseService.Error("No se pudo cargar el juego")
            }
        }
    }

    fun checkFavorite(gameId: Int) {
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(uid)
                    .collection("favoritos").document(gameId.toString()).get().await()
                _isFavorite.value = doc.exists()
            } catch (e: Exception) {
                _isFavorite.value = false
            }
        }
    }

    fun toggleFavorite(game: Game) {
        viewModelScope.launch {
            try {
                val docRef = db.collection("users").document(uid)
                    .collection("favoritos").document(game.id.toString())
                if (_isFavorite.value) {
                    docRef.delete().await()
                    _isFavorite.value = false
                } else {
                    val favorite = FavoriteGame(
                        id = game.id,
                        name = game.name,
                        backgroundImage = game.backgroundImage ?: "",
                        rating = game.rating,
                        ratingsCount = game.ratingsCount,
                        metacritic = game.metacritic,
                        released = game.released,
                        genresText = game.genres.take(2).joinToString(" · ") { it.name },
                        platformsText = game.platforms?.take(3)
                            ?.joinToString(" · ") { it.platform.name } ?: "",
                        addedAt = System.currentTimeMillis()
                    )
                    docRef.set(favorite).await()
                    _isFavorite.value = true
                }
            } catch (e: Exception) { }
        }
    }
}