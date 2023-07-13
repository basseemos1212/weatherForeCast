package com.engbassemos.weatherforeccast.favorite.view

import com.engbassemos.weatherforeccast.model.FavouriteModel

interface OnClickListner {
    fun onClickCard(favouriteModel: FavouriteModel)
    fun onDelete(favouriteModel: FavouriteModel)
}