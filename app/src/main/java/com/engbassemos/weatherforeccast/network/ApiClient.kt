package com.engbassemos.weatherforeccast.network

import com.engbassemos.weatherforeccast.model.WeatherResponse
import com.google.android.gms.common.api.Api
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient:RemoteSource {
    private val api_service: ApiService

    init {
        val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        val gson = GsonBuilder().create()
        val retrofitInstance = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        api_service = retrofitInstance.create(ApiService::class.java)
    }

    override suspend fun getWeather(
        lat: Double?,
        lon: Double?,
        units: String,
        lang: String,
        apiKey: String,
    ): WeatherResponse {
        return api_service.getWeatherResponse(lat,lon,units, lang, apiKey)
    }




}