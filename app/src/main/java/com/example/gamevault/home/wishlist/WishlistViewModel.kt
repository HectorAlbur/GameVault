package com.example.gamevault.home.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.home.wishlist.model.FavoriteGame
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WishlistViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _games = MutableStateFlow<List<FavoriteGame>>(emptyList())
    val games: StateFlow<List<FavoriteGame>> = _games.asStateFlow()

    init { loadFavorites() }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").document(uid)
                    .collection("favoritos")
                    .orderBy("addedAt", Query.Direction.DESCENDING)
                    .get().await()
                _games.value = snapshot.documents.mapNotNull {
                    it.toObject(FavoriteGame::class.java)
                }
            } catch (e: Exception) {
                _games.value = emptyList()
            }
        }
    }

    fun removeFavorite(game: FavoriteGame) {
        viewModelScope.launch {
            try {
                db.collection("users").document(uid)
                    .collection("favoritos")
                    .document(game.id.toString())
                    .delete().await()
                loadFavorites()
            } catch (e: Exception) { }
        }
    }
}