package com.engbassemos.weatherforeccast.network

import com.engbassemos.weatherforeccast.model.WeatherResponse

sealed class ApiState{
    class Success(val weather : WeatherResponse) : ApiState()
    class Failure(val msg :Throwable) :ApiState()
    object Loading : ApiState()
}
