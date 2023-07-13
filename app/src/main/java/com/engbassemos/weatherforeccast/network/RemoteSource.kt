package com.engbassemos.weatherforeccast.network

import com.engbassemos.weatherforeccast.model.WeatherResponse

interface RemoteSource {
    suspend fun getWeather(lat:Double?,lon:Double?,units:String,lang:String,apiKey:String): WeatherResponse
}