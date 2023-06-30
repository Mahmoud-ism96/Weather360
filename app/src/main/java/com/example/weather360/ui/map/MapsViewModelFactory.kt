package com.example.weather360.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather360.model.RepositoryInterface

class MapsViewModelFactory(private val _repo: RepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MapsViewModel::class.java)){
            MapsViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("ViewModel Class not Found")
        }
    }
}