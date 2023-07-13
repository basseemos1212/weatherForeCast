package com.engbassemos.weatherforeccast.database

import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.FavouriteModel
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    fun getFavWeather(): Flow<List<FavouriteModel>>
    suspend fun insertFavWeather(weather: FavouriteModel)
    suspend fun deleteFavWeather(weather: FavouriteModel)
    fun getAlertWeather(): Flow<List<AlertModel>>
    suspend fun insertAlertWeather(alertModel: AlertModel)
    suspend fun deleteAlertWeather(alertModel: AlertModel)
}