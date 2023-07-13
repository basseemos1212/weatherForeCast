package com.engbassemos.weatherforeccast.data_source

import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.FavouriteModel
import com.engbassemos.weatherforeccast.model.Repository
import com.engbassemos.weatherforeccast.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository(
    private val fakeData: MutableList<FavouriteModel> = mutableListOf(
        FavouriteModel(1, 23.1, 43.5),
        FavouriteModel(2, 23.1, 43.5),
        FavouriteModel(3, 23.1, 43.5)
    )
) : Repository {

    val fav = FavouriteModel(1, 23.1, 43.5)

    override fun getFavFromRoom(): Flow<List<FavouriteModel>> {
        return flow { emit(fakeData) }
    }

    override suspend fun insertFav(favouriteModel: FavouriteModel) {
        fakeData.add(fav)
    }

    override suspend fun deleteFav(favouriteModel: FavouriteModel) {
        fakeData.remove(fav)
    }

    override fun getAlertsFromRoom(): Flow<List<AlertModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alertModel: AlertModel) {
        // No implementation needed for the fake repository
    }

    override suspend fun deleteAlert(alertModel: AlertModel) {
        // No implementation needed for the fake repository
    }

    override suspend fun getWeatherFromRetrofit(
        lat: Double?,
        lon: Double?,
        units: String,
        lang: String,
        apiKey: String
    ): Flow<WeatherResponse>? {
        TODO("Not yet implemented")
    }
}
