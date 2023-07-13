package com.engbassemos.weatherforeccast.favorite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engbassemos.weatherforeccast.database.FavoriteState
import com.engbassemos.weatherforeccast.model.FavouriteModel
import com.engbassemos.weatherforeccast.model.Repository
import com.engbassemos.weatherforeccast.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: Repository) : ViewModel() {
    private var favoriteState: MutableStateFlow<FavoriteState> =
        MutableStateFlow(FavoriteState.Loading)
    val fav: StateFlow<FavoriteState> = favoriteState
    fun getFavsFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFavFromRoom()?.catch { exception ->
                favoriteState.value = FavoriteState.Failure(exception)
            }?.collect { res ->
                favoriteState.value = FavoriteState.Success(res)
            }
        }
    }

    fun insertFav(favouriteModel: FavouriteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertFav(favouriteModel)
        }

    }

    fun deleteFav(favouriteModel: FavouriteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFav(favouriteModel)
        }

    }

}