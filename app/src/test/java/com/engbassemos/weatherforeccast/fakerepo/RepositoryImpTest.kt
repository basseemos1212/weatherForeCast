package com.engbassemos.weatherforeccast.fakerepo

import com.engbassemos.weatherforeccast.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class RepositoryImpTest {
    val firstFav = FavouriteModel(1, 23.1, 43.5)
    val secondFav = FavouriteModel(2, 21.1, 21.5)
    val thirdFav = FavouriteModel(3, 23.1, 12.5)
    val response = WeatherResponse(
        current = Current(
            sunrise = 32131,
            temp = 25.5,
            visibility = 10000,
            uvi = 8.2,
            pressure = 1012,
            clouds = 30,

            dt = 2,

            weather = listOf(

            ),
            humidity = 50,
            dew_point = 2.3,
            feels_like = 3.2,
            sunset = 2, wind_deg = 1,
            wind_speed = 3.2


            ),
        timezone = "Alexandria/Egypt",
        timezone_offset = -14400,
        daily = listOf(
            Daily(
                dt = 1626105600,
                temp = Temp(
                    min = 20.2,
                    max = 28.5,
                    eve = 2.2,
                    night = 2.2,
                    day = 2.2,
                    morn = 2.2
                ),
                moonset = 2,
                sunrise = 2,
                moon_phase = 2.2,
                uvi = 2.2,
                moonrise = 2,
                pressure = 2,
                clouds = 2,
                feels_like = FeelsLike(
                    eve = 2.2,
                    night = 2.2,
                    day = 2.2,
                    morn = 2.2
                ),
                wind_gust = 2.2,
                dew_point = 2.2,
                sunset = 2,
                weather = listOf(
                    Weather(
                        icon = "01d",
                        description = "Clear sky",
                        main = "Clear",
                        id = 800
                    )
                ),
                rain = 2.2,
                snow = 2.2,
                humidity = 2,
                wind_speed = 2.2,
                pop = 2.2,
                wind_deg = 2
            )
        ),
        lon = 4.4,
        alerts = listOf(),
        hourly = listOf(
            Hourly(
                dt = 1626123600,
                temp = 24.8,
                visibility = 8000,
                uvi = 7.5,
                pressure = 1011,
                clouds = 40,
                feels_like = 2.2,
                wind_gust = 2.2,
                dew_point = 2.2,
                rain = Rain(2.2),
                pop = 2.2,
                snow = Snow(2.2),


                weather = listOf(
                    Weather(
                        icon = "01d",
                        description = "Clear sky",
                        main = "Clear",
                        id = 800
                    )
                ),
                humidity = 60,
                wind_speed = 2.2,
                wind_deg = 2
            )
        ),
        lat = 44.4


    )
    val localList= mutableListOf(firstFav,secondFav,thirdFav)
    lateinit var fakeConcreteLocalSource: FakeLocalSource
    lateinit var fakeRemoteSource: FakeRemoteSource
    lateinit var repository: RepositoryImp
    @Before
    fun setup(){
        fakeConcreteLocalSource= FakeLocalSource(localList)
        fakeRemoteSource= FakeRemoteSource(response)
        repository = RepositoryImp(fakeRemoteSource, fakeConcreteLocalSource)
    }
    @Test
    fun getFavFromRoom() = runBlockingTest {
        // Given
        val expectedFavorites = localList

        // When
        val favoritesFlow = repository.getFavFromRoom()
        val actualFavorites = mutableListOf<FavouriteModel>()
        favoritesFlow.collect { favorites ->
            actualFavorites.addAll(favorites)
        }

        // Then
        Assert.assertEquals(expectedFavorites, actualFavorites)
    }
    @Test
    fun insertFav() = runBlockingTest {
        // Given
        val newFavorite = FavouriteModel(3, 4321.0, 5678.0)

        // When
        repository.insertFav(newFavorite)
        val favoritesFlow = repository.getFavFromRoom()
        val favorites = mutableListOf<FavouriteModel>()
        favoritesFlow.collect { favs ->
            favorites.addAll(favs)
        }

        // Then
        Assert.assertTrue(favorites.contains(newFavorite))
    }
    @Test
    fun deleteFav() = runBlockingTest {
        val favoriteToDelete = secondFav

        repository.deleteFav(favoriteToDelete)
        val favoritesFlow = repository.getFavFromRoom()
        val favorites = mutableListOf<FavouriteModel>()
        favoritesFlow.collect { favs ->
            favorites.addAll(favs)
        }

        // Then
        Assert.assertFalse(favorites.contains(favoriteToDelete))
    }
    @Test
    fun getWeatherFromRoom() = runBlockingTest {
        // Given
        val lat = 12.34
        val lon = 56.78
        val exclude = "minutely"
        val units = "metric"
        val lang = "en"
        val expectedResponse = response
        println(expectedResponse)

        // When
        var actualResponse: WeatherResponse? = null
        repository.getWeatherFromRetrofit(lat, lon, exclude, units, lang,)?.collect { response ->
            actualResponse = response
            println(actualResponse)

        }

        // Then
        Assert.assertEquals(expectedResponse, actualResponse)
    }

}

