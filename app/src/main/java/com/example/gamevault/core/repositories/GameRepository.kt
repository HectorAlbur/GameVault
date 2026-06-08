package com.example.gamevault.core.repositories

import com.example.gamevault.core.ResponseService
import com.example.gamevault.core.model.Game
import com.example.gamevault.core.network.ApiClient
import com.example.gamevault.core.network.GameService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class GameRepository : GameService {

    private val api = ApiClient.api

    override suspend fun getGames(): ResponseService<List<Game>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getGames()
                ResponseService.Success(response.results)
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> ResponseService.Error("API key inválida")
                    429 -> ResponseService.Error("Demasiadas peticiones, intenta más tarde")
                    500 -> ResponseService.Error("Error del servidor RAWG")
                    else -> ResponseService.Error("Error HTTP ${e.code()}")
                }
            } catch (e: Exception) {
                ResponseService.Error("Sin conexión: ${e.localizedMessage}")
            }
        }

    override suspend fun searchGames(query: String): ResponseService<List<Game>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.searchGames(query = query)
                ResponseService.Success(response.results)
            } catch (e: HttpException) {
                ResponseService.Error("Error HTTP ${e.code()}")
            } catch (e: Exception) {
                ResponseService.Error("Sin conexión: ${e.localizedMessage}")
            }
        }
}