package com.engbassemos.weatherforeccast.database

import com.engbassemos.weatherforeccast.model.FavouriteModel

sealed class FavoriteState{
    class Success(val weather : List<FavouriteModel>) : FavoriteState()
    class Failure(val msg :Throwable) : FavoriteState()
    object Loading : FavoriteState()
}

