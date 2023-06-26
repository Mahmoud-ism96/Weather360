package com.example.weather360.model

import com.example.weather360.db.LocalSource
import com.example.weather360.network.RemoteSource
import kotlinx.coroutines.flow.Flow

class Repository private constructor(
    private var remoteSource: RemoteSource
) : RepositoryInterface {

    companion object {
        private var INSTANCE: Repository? = null
        fun getInstance(
            remoteSource: RemoteSource
        ): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(remoteSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getForecast(): Flow<Forecast> {
        return remoteSource.getForecast()
    }

}