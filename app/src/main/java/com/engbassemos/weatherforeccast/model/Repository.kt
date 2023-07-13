package com.engbassemos.weatherforeccast.model

import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getFavFromRoom(): Flow<List<FavouriteModel>>
    suspend fun insertFav(favouriteModel: FavouriteModel)
    suspend fun deleteFav(favouriteModel: FavouriteModel)
    fun getAlertsFromRoom(): Flow<List<AlertModel>>
    suspend fun insertAlert(alertModel: AlertModel)
    suspend fun deleteAlert(alertModel: AlertModel)
    suspend fun getWeatherFromRetrofit(
        lat: Double?,
        lon: Double?,
        units: String,
        lang: String,
        apiKey: String
    ): Flow<WeatherResponse>?
}