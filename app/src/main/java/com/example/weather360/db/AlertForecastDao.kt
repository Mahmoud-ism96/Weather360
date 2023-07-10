package com.example.weather360.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weather360.model.AlertForecast
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertForecastDao {

    @Query("SELECT * FROM alertforecast")
    fun getAll(): Flow<List<AlertForecast>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alertForecast: AlertForecast?)

    @Update
    suspend fun update(alertForecast: AlertForecast)

    @Delete
    suspend fun delete(alertForecast: AlertForecast)

    @Query("DELETE FROM alertforecast WHERE id = :id")
    suspend fun deleteById(id: Int)
}
