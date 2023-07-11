package com.example.weather360.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather360.model.FavoriteLocation
import com.example.weather360.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val _repo: RepositoryInterface) : ViewModel() {
    private val _locations = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val locations: StateFlow<List<FavoriteLocation>>
        get() = _locations

    init {
        getStoredLocations()
    }

    fun getStoredLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getStoredLocations().collect { locations ->
                _locations.value = locations
            }
        }
    }

    fun removeFromFav(favoriteLocation: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteLocation(favoriteLocation)
        }
    }
}