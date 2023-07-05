package com.example.weather360.util

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesSingleton {
    private const val PREFERENCE_NAME = "SharedPref"
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun getEditor(): SharedPreferences.Editor {
        if (!::sharedPreferences.isInitialized) {
            throw UninitializedPropertyAccessException("SharedPreferencesSingleton has not been initialized.")
        }
        return sharedPreferences.edit()
    }

    fun writeString(key: String, value: String) {
        getEditor().putString(key, value).apply()
    }

    fun readString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun writeBoolean(key: String, value: Boolean) {
        getEditor().putBoolean(key, value).apply()
    }

    fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun writeFloat(key: String, value: Float) {
        getEditor().putFloat(key, value).apply()
    }

    fun readFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }
}
