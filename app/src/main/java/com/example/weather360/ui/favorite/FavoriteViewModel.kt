package com.example.weather360.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather360.model.FavoriteLocation
import com.example.weather360.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(private val _repo: RepositoryInterface) : ViewModel() {
    private val _locations = MutableLiveData<List<FavoriteLocation>>()
    val locations: LiveData<List<FavoriteLocation>>
        get() = _locations

    init {
        getStoredLocations()
    }

    private fun getStoredLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getStoredLocations().collect() {
                _locations.postValue(it)
            }
        }
    }

    fun removeFromFav(favoriteLocation: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteLocation(favoriteLocation)
        }
    }
}