package com.engbassemos.weatherforeccast.favorite.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.engbassemos.weatherforeccast.R
import com.engbassemos.weatherforeccast.alert.view.AlertFragment
import com.engbassemos.weatherforeccast.database.ConcreteLocalSource
import com.engbassemos.weatherforeccast.database.FavoriteState
import com.engbassemos.weatherforeccast.databinding.FragmentFavoriteBinding
import com.engbassemos.weatherforeccast.databinding.FragmentHomeBinding
import com.engbassemos.weatherforeccast.favorite.viewModel.FavoriteViewModel
import com.engbassemos.weatherforeccast.favorite.viewModel.FavoriteViewModelFactory
import com.engbassemos.weatherforeccast.home.view.HomeFragment
import com.engbassemos.weatherforeccast.model.FavouriteModel
import com.engbassemos.weatherforeccast.model.Repository
import com.engbassemos.weatherforeccast.model.RepositoryImp
import com.engbassemos.weatherforeccast.network.ApiClient
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment(), OnClickListner {
    lateinit var binding: FragmentFavoriteBinding
    lateinit var favoriteAdapter: FavoriteAdapter
    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    lateinit var preferences: SharedPreferences
    lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter = FavoriteAdapter(emptyList(), requireContext(), this)
        preferences = requireActivity().getSharedPreferences("PrefFile", Context.MODE_PRIVATE)
        editor = preferences.edit()
        favoriteViewModelFactory = FavoriteViewModelFactory(
            RepositoryImp.getInstance(
                ApiClient(),
                ConcreteLocalSource.getInstance(requireContext())
            )
        )
        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get((FavoriteViewModel::class.java))
        favoriteViewModel.getFavsFromRoom()
        lifecycleScope.launch{
            favoriteViewModel.fav.collect{
                res->
                when(res){
                    is FavoriteState.Success->{
                        res.weather.let {
                            favoriteAdapter.submitList(it)
                            binding.favRecycleView.adapter=favoriteAdapter
                        }
                    }
                    else -> {}
                }
            }
        }
        binding.fabAdd.setOnClickListener{
            editor.putString("mapDestination","fav").apply()
                Navigation.findNavController(view).navigate(R.id.action_favoriteFragment_to_googleMapFragment)
        }
    }

    override fun onClickCard(favouriteModel: FavouriteModel) {
        editor.putString("homeDestination","fav").apply()
//        val currentView=view
//        if (currentView != null) {
//            Navigation.findNavController(currentView).navigate(R.id.action_favoriteFragment_to_homeFragment)
//        }
        val fragment = HomeFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host, fragment)
        transaction.commit()
    }

    override fun onDelete(favouriteModel: FavouriteModel) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setTitle("Delete From Fav")
        builder.setMessage("Are you sure")
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            favoriteViewModel.deleteFav(favouriteModel)
        }
        builder.setNegativeButton(android.R.string.cancel) { _, _ -> }
        builder.show()
    }


}