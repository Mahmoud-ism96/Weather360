package com.example.weather360.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weather360.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocationDao {
    @Query("SELECT * from favoritelocation")
    fun getAll(): Flow<List<FavoriteLocation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favLocation: FavoriteLocation?)

    @Update
    suspend fun update(favLocation: FavoriteLocation)

    @Delete
    suspend fun delete(favLocation: FavoriteLocation): Int
}