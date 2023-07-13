package com.engbassemos.weatherforeccast.database

import android.content.Context
import android.provider.ContactsContract.Data
import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.FavouriteModel
import kotlinx.coroutines.flow.Flow

class ConcreteLocalSource(var context: Context) : LocalSource {
    private val dao: Dao by lazy {
        val dataBase: DataBase = DataBase.getInstance(context)
        dataBase.getWeatherDao()
    }

    companion object {
        private var instance: ConcreteLocalSource? = null
        fun getInstance(context: Context): ConcreteLocalSource {
            return instance ?: synchronized(this) {
                val Instance = ConcreteLocalSource(context)
                instance = Instance
                Instance
            }
        }
    }

    override fun getFavWeather(): Flow<List<FavouriteModel>> {
        return dao.getFavWeather()
    }

    override suspend fun insertFavWeather(weather: FavouriteModel) {
        dao.insertFavWeather(weather)
    }

    override suspend fun deleteFavWeather(weather: FavouriteModel) {
        dao.deleteFavWeather(weather)
    }

    override fun getAlertWeather(): Flow<List<AlertModel>> {
        return dao.getAlertWeathers()
    }

    override suspend fun insertAlertWeather(alertModel: AlertModel) {
        dao.insertAlertWeather(alertModel)
    }

    override suspend fun deleteAlertWeather(alertModel: AlertModel) {
       dao.deleteAlertWeather(alertModel)
    }

}