package com.example.gamevault.core.network

import com.example.gamevault.core.model.Game
import com.example.gamevault.core.model.GamesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GameAPI {

    @GET("games")
    suspend fun getGames(
        @Query("key") key: String = ApiClient.API_KEY,
        @Query("page_size") pageSize: Int = 20,
        @Query("ordering") ordering: String = "-added"
    ): GamesResponse

    @GET("games")
    suspend fun searchGames(
        @Query("key") key: String = ApiClient.API_KEY,
        @Query("search") query: String,
        @Query("page_size") pageSize: Int = 20
    ): GamesResponse

    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") id: Int,
        @Query("key") key: String = ApiClient.API_KEY
    ): Game
}