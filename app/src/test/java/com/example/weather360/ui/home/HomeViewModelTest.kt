package com.example.weather360.ui.home

import app.cash.turbine.test
import com.example.weather360.datasource.FakeRepo
import com.example.weather360.network.ApiStatus
import com.example.weather360.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repository: FakeRepo

    @Before
    fun setupHomeViewModel() {
        repository = FakeRepo()
        homeViewModel = HomeViewModel(repository)
    }

    @Test
    fun getForecastSuccess() = runBlockingTest {
        //WHEN
        homeViewModel.getForecast(0.0, 0.0, "en")

        var result: ApiStatus? = null
        homeViewModel.forecast.test {
            result = this.awaitItem()
        }

        // THEN
        if (result is ApiStatus.Success) {
            assertEquals(0.0, (result as ApiStatus.Success).forecast.lat, 0.0)
        }
    }

    @Test
    fun getForecastLoading() = runBlockingTest {
        // WHEN
        homeViewModel.getForecast(0.0, 0.0, "en")

        var result: ApiStatus? = null
        homeViewModel.forecast.test {
            result = this.awaitItem()
        }

        // THEN
        assertTrue(result is ApiStatus.Loading)
    }
}
