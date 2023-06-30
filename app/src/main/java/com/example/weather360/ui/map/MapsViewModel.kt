package com.example.weather360.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather360.model.FavoriteLocation
import com.example.weather360.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel (private val _repo: RepositoryInterface) : ViewModel() {

    fun addToFav(favoriteLocation: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.insertLocation(favoriteLocation)
        }
    }
}