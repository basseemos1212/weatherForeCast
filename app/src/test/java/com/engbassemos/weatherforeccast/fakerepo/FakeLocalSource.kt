package com.engbassemos.weatherforeccast.fakerepo

import com.engbassemos.weatherforeccast.database.LocalSource
import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.FavouriteModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource(
    private val favList: MutableList<FavouriteModel> = mutableListOf<FavouriteModel>(),
    private val alertList: MutableList<AlertModel> = mutableListOf<AlertModel>()
) : LocalSource {


    override fun getFavWeather(): Flow<List<FavouriteModel>> {
        return flowOf(favList)
    }

    override suspend fun insertFavWeather(weather: FavouriteModel) {
        favList.add(weather)
    }

    override suspend fun deleteFavWeather(weather: FavouriteModel) {
        favList.remove(weather)
    }

    override fun getAlertWeather(): Flow<List<AlertModel>> {
        return flowOf(alertList)
    }

    override suspend fun insertAlertWeather(alertModel: AlertModel) {
        alertList.add(alertModel)
    }

    override suspend fun deleteAlertWeather(alertModel: AlertModel) {
        alertList.remove(alertModel)
    }
}