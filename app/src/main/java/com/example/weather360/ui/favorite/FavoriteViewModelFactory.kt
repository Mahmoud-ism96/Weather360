package com.example.weather360.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather360.model.RepositoryInterface
import com.example.weather360.ui.home.HomeViewModel

class FavoriteViewModelFactory(private val _repo: RepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not Found")
        }
    }
}