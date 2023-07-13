package com.engbassemos.weatherforeccast.favorite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.engbassemos.weatherforeccast.home.viewModel.HomeViewModel
import com.engbassemos.weatherforeccast.model.Repository

class FavoriteViewModelFactory (private val repo : Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(repo) as T
        }else {
            throw java.lang.IllegalArgumentException("viewModel class not found")
        }
    }}


