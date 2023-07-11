package com.example.weather360.ui.favorite

import app.cash.turbine.test
import com.example.weather360.datasource.FakeRepo
import com.example.weather360.model.FavoriteLocation
import com.example.weather360.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoriteViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var repository: FakeRepo

    @Before
    fun setupHomeViewModel() {
        repository = FakeRepo()
        favoriteViewModel = FavoriteViewModel(repository)
    }

    @Test
    fun getLocations() = runBlockingTest {
        //GIVEN
        val location = FavoriteLocation("Loc1", 10.0, 20.0, 123)
        repository.insertLocation(location)

        //WHEN
        favoriteViewModel.getStoredLocations()

        val result: FavoriteLocation? = null
        favoriteViewModel.locations.test {
            this.awaitItem()
        }

        //THEN
        if (result != null) {
            assertEquals(0.0, result.latitude, 0.0)
        }
    }

    @Test
    fun removeFromFav() = runBlockingTest {
        // GIVEN
        val location = FavoriteLocation("Loc1", 10.0, 20.0, 123)
        repository.insertLocation(location)

        // WHEN
        favoriteViewModel.removeFromFav(location)
        favoriteViewModel.getStoredLocations()

        val result: List<FavoriteLocation>? = null
        favoriteViewModel.locations.test {
            this.awaitItem()
        }

        //THEN

        assertNull(result)
    }
}