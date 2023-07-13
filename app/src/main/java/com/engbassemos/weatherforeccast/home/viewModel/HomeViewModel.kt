package com.engbassemos.weatherforeccast.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engbassemos.weatherforeccast.model.Repository
import com.engbassemos.weatherforeccast.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel (private val repo: Repository): ViewModel(){
    private var weatherState : MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val weather: StateFlow<ApiState> = weatherState
    fun getWeatherFromRetrofit(lat:Double?,lon:Double?,units:String,lang:String,apiKey:String)
    {
        viewModelScope.launch(Dispatchers.IO) {

            repo.getWeatherFromRetrofit(lat,lon,units,lang, apiKey)
                ?.catch { e->
                    weatherState.value=ApiState.Failure(e) }
                ?.collect{ data->
                    weatherState.value=ApiState.Success(data)
                }
        }
    }
}