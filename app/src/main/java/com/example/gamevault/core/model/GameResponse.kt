package com.example.gamevault.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GamesResponse(
    val count: Int = 0,
    val results: List<Game> = emptyList()
)

@Parcelize
data class Game(
    val id: Int = 0,
    val name: String = "",
    @SerializedName("background_image") val backgroundImage: String? = null,
    val rating: Float = 0f,
    @SerializedName("ratings_count") val ratingsCount: Int = 0,
    val metacritic: Int? = null,
    val released: String? = null,
    val description: String? = null,
    val playtime: Int = 0,
    val genres: List<Genre> = emptyList(),
    val platforms: List<PlatformWrapper>? = null,
    val developers: List<Developer>? = null
) : Parcelable

@Parcelize
data class Genre(val id: Int = 0, val name: String = "") : Parcelable

@Parcelize
data class PlatformWrapper(val platform: Platform = Platform()) : Parcelable

@Parcelize
data class Platform(val id: Int = 0, val name: String = "") : Parcelable

@Parcelize
data class Developer(val id: Int = 0, val name: String = "") : Parcelable