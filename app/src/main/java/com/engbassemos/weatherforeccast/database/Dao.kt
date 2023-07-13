package com.engbassemos.weatherforeccast.database
import androidx.room.*
import androidx.room.Dao
import com.engbassemos.weatherforeccast.favorite.viewModel.FavoriteViewModel
import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.FavouriteModel
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    @Query("Select * FROM FavWeather")
    fun getFavWeather(): Flow<List<FavouriteModel>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavWeather(weather: FavouriteModel)
    @Delete
    suspend fun deleteFavWeather(weather: FavouriteModel)
    @Query("Select * FROM AlertWeather")
    fun getAlertWeathers(): Flow<List<AlertModel>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlertWeather(alertModel: AlertModel)
    @Delete
    suspend fun deleteAlertWeather(alertModel: AlertModel)

}