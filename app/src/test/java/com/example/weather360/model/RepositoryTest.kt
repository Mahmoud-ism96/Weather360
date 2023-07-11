package com.example.weather360.model

import com.example.weather360.datasource.FakeLocalSource
import com.example.weather360.datasource.FakeRemoteSource
import com.example.weather360.db.LocalSource
import com.example.weather360.network.RemoteSource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositoryTest {

    val location1 = FavoriteLocation(name = "Location 1", longitude = 12.34, latitude = 56.78)
    val location2 = FavoriteLocation(name = "Location 2", longitude = 23.45, latitude = 67.89)
    val location3 = FavoriteLocation(name = "Location 3", longitude = 34.56, latitude = 78.90)

    val alertForecast1 = AlertForecast(
        id = 1, time = "12:00 PM", date = "2023-07-09", longitude = 30.8027, latitude = 28.4973
    )
    val alertForecast2 = AlertForecast(
        id = 2, time = "03:30 PM", date = "2023-07-10", longitude = 30.8027, latitude = 28.4973
    )
    val alertForecast3 = AlertForecast(
        id = 3, time = "09:15 AM", date = "2023-07-11", longitude = 30.8027, latitude = 28.4973
    )

    val favoriteLocations = listOf(location1, location2, location3)
    val alertForecasts = listOf(alertForecast1, alertForecast2, alertForecast3)
    val forecast = Forecast(
        Current(
            dt = 1689105971,
            sunrise = 1689044804,
            sunset = 1689094638,
            temp = 31.71,
            feels_like = 30.39,
            pressure = 1009,
            humidity = 29,
            dew_point = 11.51,
            uvi = 0.0,
            clouds = 0,
            visibility = 10000,
            wind_speed = 4.82,
            wind_deg = 21,
            wind_gust = 0.0,
            weather = listOf()

        ), listOf(), listOf(), listOf(), 0.0, 0.0, "Cairo", 0
    )


    private lateinit var fakeLocalSource: LocalSource
    private lateinit var fakeRemoteSource: RemoteSource
    private lateinit var repository: RepositoryInterface

    @Before
    fun setUp() {
        fakeLocalSource = FakeLocalSource() as FakeLocalSource
        fakeRemoteSource = FakeRemoteSource(forecast)
        repository = Repository.getInstance(fakeRemoteSource, fakeLocalSource)
    }

    @Test
    fun getForecast() = runBlocking {
        //WHEN
        val result = repository.getForecast(forecast.lat, forecast.lon, "en")

        //THEN
        result.collectLatest { assertEquals(forecast, it) }
    }

    @Test
    fun insertLocation() = runBlocking {
        //WHEN
        repository.insertLocation(location1)

        //THEN
        repository.getStoredLocations().collectLatest {
            assertTrue(it.contains(location1))
        }

        repository.deleteLocation(location1)
    }

    @Test
    fun getStoredLocations() = runBlocking {
        //WHEN
        repository.insertLocation(location1)
        repository.insertLocation(location2)

        //THEN
        repository.getStoredLocations().collectLatest {
            assertEquals(2, it.size)
        }

        repository.deleteLocation(location1)
        repository.deleteLocation(location2)
    }

    @Test
    fun deleteLocation() = runBlocking {
        //WHEN
        repository.insertLocation(location1)
        repository.deleteLocation(location1)

        //THEN
        repository.getStoredLocations().collectLatest {
            assertFalse(it.contains(location1))
        }
    }

    @Test
    fun insertAlertForecast() = runBlocking {
        //WHEN
        repository.insertAlertForecast(alertForecast1)

        //THEN
        repository.getStoredAlertForecasts().collectLatest {
            assertTrue(it.contains(alertForecast1))
        }
    }

    @Test
    fun deleteAlertForecast() = runBlocking {
        //WHEN
        repository.insertAlertForecast(alertForecast1)
        repository.deleteAlertForecast(alertForecast1)

        //THEN
        repository.getStoredAlertForecasts().collectLatest {
            assertFalse(it.contains(alertForecast1))
        }
    }

    @Test
    fun deleteAlertForecastById() = runBlocking {
        //WHEN
        repository.insertAlertForecast(alertForecast1)
        repository.deleteAlertForecastById(alertForecast1.id)

        //THEN
        repository.getStoredAlertForecasts().collectLatest {
            assertFalse(it.contains(alertForecast1))
        }
    }

    @Test
    fun getStoredAlertForecasts() = runBlocking {
        //WHEN
        repository.insertAlertForecast(alertForecast1)
        repository.insertAlertForecast(alertForecast2)

        //THEN
        repository.getStoredAlertForecasts().collectLatest {
            assertEquals(2, it.size)
        }
    }
}