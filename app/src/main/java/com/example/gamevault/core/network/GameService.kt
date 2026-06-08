package com.example.gamevault.core.network

import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.Game

interface GameService {
    suspend fun getGames(): ResponseService<List<Game>>
    suspend fun searchGames(query: String): ResponseService<List<Game>>
}