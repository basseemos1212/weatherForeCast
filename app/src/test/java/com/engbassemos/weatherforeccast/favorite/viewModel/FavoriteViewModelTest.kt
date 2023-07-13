package com.engbassemos.weatherforeccast.favorite.viewModel

import app.cash.turbine.test
import com.engbassemos.weatherforeccast.MainDispatcherRule
import com.engbassemos.weatherforeccast.data_source.FakeRepository
import com.engbassemos.weatherforeccast.database.FavoriteState
import com.engbassemos.weatherforeccast.model.FavouriteModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoriteViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var fakeRepository: FakeRepository

    @Before
    fun setUp() {
        fakeRepository= FakeRepository()
        favoriteViewModel= FavoriteViewModel(fakeRepository)
    }



    @Test
    fun getFavsFromRoom() = runBlockingTest {
        var res = listOf<FavouriteModel>()
        favoriteViewModel.getFavsFromRoom()
        favoriteViewModel.fav.test {
            this.awaitItem().apply {
                when(this){
                    is FavoriteState.Success->{res=this.weather}

                    else -> {}
                }
                assertEquals(3,res.size)
            }
        }
    }

    @Test
    fun insertFav() = runBlockingTest(){
        favoriteViewModel.insertFav( FavouriteModel(4, 23.1, 43.5))
        var res = listOf<FavouriteModel>()
        favoriteViewModel.getFavsFromRoom()
        favoriteViewModel.fav.test {
            this.awaitItem().apply {
                when(this){
                    is FavoriteState.Success->{res=this.weather}

                    else -> {}
                }
                assertEquals(4,res.size)
            }
        }
    }

    @Test
    fun deleteFav() = runBlockingTest{
        favoriteViewModel.deleteFav( FavouriteModel(3, 23.1, 43.5))
        var res = listOf<FavouriteModel>()
        favoriteViewModel.getFavsFromRoom()
        favoriteViewModel.fav.test {
            this.awaitItem().apply {
                when(this){
                    is FavoriteState.Success->{res=this.weather}

                    else -> {}
                }
                assertEquals(2,res.size)
            }
        }

    }
}