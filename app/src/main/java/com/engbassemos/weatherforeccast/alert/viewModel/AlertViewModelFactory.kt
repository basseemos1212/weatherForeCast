package com.engbassemos.weatherforeccast.alert.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.engbassemos.weatherforeccast.favorite.viewModel.FavoriteViewModel
import com.engbassemos.weatherforeccast.model.Repository

class AlertViewModelFactory  (private val repo : Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(repo) as T
        }else {
            throw java.lang.IllegalArgumentException("viewModel class not found")
        }
    }}
