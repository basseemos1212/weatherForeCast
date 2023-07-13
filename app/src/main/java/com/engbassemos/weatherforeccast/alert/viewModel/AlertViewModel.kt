package com.engbassemos.weatherforeccast.alert.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engbassemos.weatherforeccast.database.AlertState
import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel(private val repo: Repository) : ViewModel()  {
    private var alertState: MutableStateFlow<AlertState> =
        MutableStateFlow(AlertState.Loading)
    val alert: StateFlow<AlertState> = alertState
    fun getFavsFromRoom() {
        viewModelScope.launch (Dispatchers.IO) {
            repo.getAlertsFromRoom()?.catch { exception ->
                alertState.value = AlertState.Failure(exception)
            }?.collect { res ->
                alertState.value = AlertState.Success(res)
            }
        }
    }

    fun insertFav(alertModel: AlertModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertAlert(alertModel)
        }

    }

    fun deleteFav(alertModel:AlertModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAlert(alertModel)
        }

    }

}