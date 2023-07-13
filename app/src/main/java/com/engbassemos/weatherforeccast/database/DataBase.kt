package com.engbassemos.weatherforeccast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.engbassemos.weatherforeccast.model.*

@Database(entities = [WeatherResponse::class, FavouriteModel::class , AlertModel::class], version = 2)
@TypeConverters(Converter::class)
abstract class DataBase  : RoomDatabase() {
    abstract fun getWeatherDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: DataBase? = null
        fun getInstance(context: Context): DataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, DataBase::class.java, "WeatherDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}