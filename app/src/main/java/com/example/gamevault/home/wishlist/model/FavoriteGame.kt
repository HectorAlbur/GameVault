package com.example.gamevault.home.wishlist.model

data class FavoriteGame(
    val id: Int = 0,
    val name: String = "",
    val backgroundImage: String = "",
    val rating: Float = 0f,
    val ratingsCount: Int = 0,
    val metacritic: Int? = null,
    val released: String? = null,
    val genresText: String = "",
    val platformsText: String = "",
    val addedAt: Long = 0
)