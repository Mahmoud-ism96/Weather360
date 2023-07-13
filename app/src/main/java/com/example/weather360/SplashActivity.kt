package com.example.weather360

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.weather360.databinding.ActivitySplashBinding
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_LANGUAGE
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_AR
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_EN
import com.example.weather360.util.SharedPreferencesSingleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onStart() {
        super.onStart()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        SharedPreferencesSingleton.initialize(this)

        val language = SharedPreferencesSingleton.readString(KEY_SELECTED_LANGUAGE,LANG_VALUE_EN)

        when(language){
            LANG_VALUE_EN-> {
                val locale = Locale("en")
                Locale.setDefault(locale)
                val resources: Resources = resources
                val config: Configuration = resources.configuration
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
            LANG_VALUE_AR->{

                val locale = Locale("ar")
                Locale.setDefault(locale)
                val resources: Resources = resources
                val config: Configuration = resources.configuration
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }

        lifecycleScope.launch {
            delay(1)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
