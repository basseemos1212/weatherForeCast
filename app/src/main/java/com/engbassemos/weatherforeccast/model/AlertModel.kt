package com.engbassemos.weatherforeccast.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "AlertWeather")
data class AlertModel(
    @PrimaryKey(autoGenerate = true)
    val id :Int=0,
    var startDate: Long,
    var endDate: Long,
    var startimeOfAlert: Long,
    var endTimeOfAlert: Long
)
