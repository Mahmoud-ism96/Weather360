package com.example.weather360.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather360.model.RepositoryInterface
import com.example.weather360.network.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val _repo: RepositoryInterface) : ViewModel() {
    private val _forecast: MutableStateFlow<ApiStatus> = MutableStateFlow(ApiStatus.Loading)
    val forecast: StateFlow<ApiStatus>
        get() = _forecast

    fun getForecast(latitude: Double, longitude: Double, language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getForecast(latitude, longitude, language).catch {
                _forecast.value = ApiStatus.Failure(it)
            }.collect() {
                _forecast.value = ApiStatus.Success(it)
            }
        }
    }
}