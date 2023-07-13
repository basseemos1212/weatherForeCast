package com.engbassemos.weatherforeccast.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.engbassemos.weatherforeccast.model.Repository

class ViewModelFactory (private val repo :Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repo) as T
        }else {
            throw java.lang.IllegalArgumentException("viewModel class not found")
        }
    }}


