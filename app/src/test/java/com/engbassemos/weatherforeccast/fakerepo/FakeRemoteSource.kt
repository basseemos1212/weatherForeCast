package com.engbassemos.weatherforeccast.fakerepo

import com.engbassemos.weatherforeccast.model.WeatherResponse
import com.engbassemos.weatherforeccast.network.RemoteSource

class FakeRemoteSource(private  val weatherResponse: WeatherResponse) : RemoteSource {
    override suspend fun getWeather(
        lat: Double?,
        lon: Double?,
        units: String,
        lang: String,
        apiKey: String
    ): WeatherResponse {
        return weatherResponse
    }
}