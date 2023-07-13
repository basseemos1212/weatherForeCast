package com.engbassemos.weatherforeccast.model

import com.engbassemos.weatherforeccast.database.LocalSource
import com.engbassemos.weatherforeccast.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RepositoryImp(var remote: RemoteSource, var localSource: LocalSource) :
    Repository {
    companion object {
        private var instance: Repository? = null
        fun getInstance(remote: RemoteSource, localSource: LocalSource): Repository {
            return instance ?: synchronized(this) {
                val Instance = RepositoryImp(remote, localSource)
                instance = Instance
                Instance
            }
        }
    }

    override fun getFavFromRoom(): Flow<List<FavouriteModel>> {
        return localSource.getFavWeather()
    }

    override suspend fun insertFav(favouriteModel: FavouriteModel) {
        localSource.insertFavWeather(favouriteModel)
    }

    override suspend fun deleteFav(favouriteModel: FavouriteModel) {
        localSource.deleteFavWeather(favouriteModel)
    }

    override fun getAlertsFromRoom(): Flow<List<AlertModel>> {
        return localSource.getAlertWeather()
    }

    override suspend fun insertAlert(alertModel: AlertModel) {
        localSource.insertAlertWeather(alertModel)
    }

    override suspend fun deleteAlert(alertModel: AlertModel) {
        localSource.deleteAlertWeather(alertModel)
    }

    override suspend fun getWeatherFromRetrofit(
        lat: Double?,
        lon: Double?,
        units: String,
        lang: String,
        apiKey: String
    ): Flow<WeatherResponse>? {
        return flowOf(remote.getWeather(lat, lon, units, lang, apiKey))
    }
}