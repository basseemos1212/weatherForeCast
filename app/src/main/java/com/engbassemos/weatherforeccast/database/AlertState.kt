package com.engbassemos.weatherforeccast.database

import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.FavouriteModel

sealed class AlertState {
    class Success(val alerts: List<AlertModel>) : AlertState()
    class Failure(val msg: Throwable) : AlertState()
    object Loading : AlertState()
}

