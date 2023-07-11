package com.example.weather360.ui.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather360.model.AlertForecast
import com.example.weather360.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertViewModel(private val _repo: RepositoryInterface) : ViewModel() {
    private val _alerts: MutableStateFlow<List<AlertForecast>> = MutableStateFlow(emptyList())
    val alerts: StateFlow<List<AlertForecast>>
        get() = _alerts


    init {
        getStoredAlerts()
    }

    private fun getStoredAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getStoredAlertForecasts().collect { alerts ->
                _alerts.emit(alerts)
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