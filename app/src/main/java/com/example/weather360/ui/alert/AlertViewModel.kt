package com.example.weather360.ui.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather360.model.AlertForecast
import com.example.weather360.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertViewModel(private val _repo: RepositoryInterface) : ViewModel() {
    private val _alerts= MutableLiveData<List<AlertForecast>>()
    val alerts: LiveData<List<AlertForecast>>
        get() = _alerts

    init {
        getStoredAlerts()
    }

    private fun getStoredAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getStoredAlertForecasts().collect() {
                _alerts.postValue(it)
            }
        }
    }

    fun insertAlert(alertForecast: AlertForecast) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.insertAlertForecast(alertForecast)
        }
    }

    fun deleteAlert(alertForecast: AlertForecast) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteAlertForecast(alertForecast)
        }
    }
}