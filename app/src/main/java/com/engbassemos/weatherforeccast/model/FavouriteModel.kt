package com.engbassemos.weatherforeccast.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "FavWeather")
data class FavouriteModel(
    @PrimaryKey(autoGenerate = true)
    val id :Int=0,
    val latitude: Double,
    val longitude: Double
)
