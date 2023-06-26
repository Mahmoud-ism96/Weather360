package com.example.weather360.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather360.model.RepositoryInterface

class HomeViewModelFactory(private val _repo: RepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("ViewModel Class not Found")
        }
    }
}