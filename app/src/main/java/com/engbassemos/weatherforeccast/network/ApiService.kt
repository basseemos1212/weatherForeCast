package com.engbassemos.weatherforeccast.network

import com.engbassemos.weatherforeccast.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("onecall")
    suspend fun getWeatherResponse(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("units") units: String,
        @Query("lang") lang: String, // stands for language
        @Query("appid") apiKey: String,
    ): WeatherResponse
}