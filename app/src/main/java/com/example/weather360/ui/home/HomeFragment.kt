package com.example.weather360.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather360.MainActivity
import com.example.weather360.R
import com.example.weather360.databinding.FragmentHomeBinding
import com.example.weather360.db.ConcreteLocalSource
import com.example.weather360.enums.MapSelectionType
import com.example.weather360.model.FavoriteLocation
import com.example.weather360.model.Forecast
import com.example.weather360.model.Repository
import com.example.weather360.network.ApiClient
import com.example.weather360.network.ApiStatus
import com.example.weather360.util.CommonUtils
import com.example.weather360.util.CommonUtils.Companion.KEY_CURRENT_LAT
import com.example.weather360.util.CommonUtils.Companion.KEY_CURRENT_LONG
import com.example.weather360.util.CommonUtils.Companion.KEY_FIRST_STARTUP
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_LOCATION
import com.example.weather360.util.CommonUtils.Companion.capitalizeWords
import com.example.weather360.util.CommonUtils.Companion.fromUnixToString
import com.example.weather360.util.SharedPreferencesSingleton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var _viewModel: HomeViewModel
    private lateinit var recyclerAdapter: HomeAdapter

    private var favLocation: FavoriteLocation? = null

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var lastLongitude by Delegates.notNull<Double>()
    private var lastLatitude by Delegates.notNull<Double>()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getLastLocation()
        } else {
            //TODO: handle the ELSE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val _viewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                ApiClient, ConcreteLocalSource.getInstance(requireContext())
            )
        )

        _viewModel = ViewModelProvider(this, _viewModelFactory)[HomeViewModel::class.java]

        SharedPreferencesSingleton.initialize(requireContext())

        initRecyclerView()

        initAPIRequest()

        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (isFirstStartUp()) {
            startUpDialog()

            SharedPreferencesSingleton.writeString(
                CommonUtils.KEY_SELECTED_TEMP_UNIT, getString(R.string.celsius)
            )
            SharedPreferencesSingleton.writeString(
                CommonUtils.KEY_SELECTED_WIND_SPEED, getString(R.string.meter_sec)
            )
            SharedPreferencesSingleton.writeString(
                CommonUtils.KEY_SELECTED_LANGUAGE, getString(R.string.english)
            )

        } else {
            requestForecast()
        }

        return root
    }

    private fun requestForecast() {
        Log.i("TAG", "requestForecast: Started")
        favLocation = HomeFragmentArgs.fromBundle(requireArguments()).favLocation
        if (favLocation == null) {
            when (SharedPreferencesSingleton.readString(
                KEY_SELECTED_LOCATION, getString(R.string.gps)
            )) {
                getString(R.string.gps) -> {
                    if (checkLocationPermission()) {
                        getLastLocation()
                    }
                }

                getString(R.string.map) -> {
                    val currentLatitude =
                        SharedPreferencesSingleton.readFloat(KEY_CURRENT_LAT, 0.0f).toDouble()
                    val currentLongitude =
                        SharedPreferencesSingleton.readFloat(KEY_CURRENT_LONG, 0.0f).toDouble()
                    Log.i("TAG", "requestForecast: $currentLatitude $currentLongitude")
                    _viewModel.getForecast(currentLatitude, currentLongitude)
                }
            }
        } else {
            (requireActivity() as MainActivity).updateVisiblityOnNavigation(View.GONE, View.VISIBLE)
            _viewModel.getForecast(favLocation!!.latitude, favLocation!!.longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        val locationRequest = LocationRequest.create().apply {
            interval = 0
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lastLatitude = locationResult.lastLocation.latitude
                lastLongitude = locationResult.lastLocation.longitude
                _viewModel.getForecast(lastLatitude, lastLongitude)
            }
        }

        fusedClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    private fun checkLocationPermission(): Boolean {
        var result = false
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            result = true
        } else {
            requestPermission()
        }
        return result
    }

    private fun requestPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun initAPIRequest() {
        lifecycleScope.launch {
            _viewModel.forecast.collectLatest {
                when (it) {
                    is ApiStatus.Success -> {
                        val forecast = it.forecast
                        showData(forecast)

                        binding.homeShowDaysClick.setOnClickListener {
                            val navigationAction =
                                HomeFragmentDirections.actionNavHomeToWeeklyFragment(forecast)
                            findNavController().navigate(navigationAction)
                        }

                        recyclerAdapter.submitList(it.forecast.hourly)
                    }

                    is ApiStatus.Failure -> Log.i("Main", "Error: ${it.err}")
                    else -> {
                        Log.i("Main", "No Response")
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        recyclerAdapter = HomeAdapter(requireContext())

        binding.rvHomeHourly.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
    }

    private fun isFirstStartUp(): Boolean {
        return SharedPreferencesSingleton.readBoolean(getString(R.string.first_startup), true)
    }

    private fun showData(forecast: Forecast) {
        binding.apply {

            when (forecast.current.weather[0].icon) {
                "01d" -> lottieMain.setAnimation(R.raw.weather_sunny)
                "02d" -> lottieMain.setAnimation(R.raw.weather_night)
                "01n" -> lottieMain.setAnimation(R.raw.weather_partly_cloudy)
                "02n" -> lottieMain.setAnimation(R.raw.weather_cloudynight)
                "03d" -> lottieMain.setAnimation(R.raw.weather_windy)
                "03n" -> lottieMain.setAnimation(R.raw.weather_windy)
                "04d" -> lottieMain.setAnimation(R.raw.weather_windy)
                "04n" -> lottieMain.setAnimation(R.raw.weather_windy)
                "09d" -> lottieMain.setAnimation(R.raw.weather_partly_shower)
                "09n" -> lottieMain.setAnimation(R.raw.weather_rainynight)
                "10d" -> lottieMain.setAnimation(R.raw.weather_partly_shower)
                "10n" -> lottieMain.setAnimation(R.raw.weather_rainynight)
                "11d" -> lottieMain.setAnimation(R.raw.weather_stormshowersday)
                "11n" -> lottieMain.setAnimation(R.raw.weather_storm)
                "13d" -> lottieMain.setAnimation(R.raw.weather_snow_sunny)
                "13n" -> lottieMain.setAnimation(R.raw.weather_snownight)
                "50d" -> lottieMain.setAnimation(R.raw.weather_foggy)
                "50n" -> lottieMain.setAnimation(R.raw.weather_mist)
            }

            val cityName: List<String> = forecast.timezone.split("/")
            tvCityName.text = cityName[1]
            tvHomeCurrentDate.text = fromUnixToString(forecast.current.dt)

            when (SharedPreferencesSingleton.readString(
                CommonUtils.KEY_SELECTED_TEMP_UNIT, getString(R.string.celsius)
            )) {
                getString(R.string.celsius) -> {
                    tvHomeTemp.text = (forecast.current.temp - 273.15).toInt().toString()
                    tvHomeFeelsLike.text = ("/${(forecast.current.feels_like - 273.15).toInt()}°C")
                }

                getString(R.string.kelvin) -> {
                    tvHomeTemp.text = forecast.current.temp.toInt().toString()
                    tvHomeFeelsLike.text = ("/${forecast.current.feels_like.toInt()} K")
                }

                getString(R.string.fahrenheit) -> {
                    tvHomeTemp.text =
                        ((forecast.current.temp - 273.15) * 9 / 5 + 32).toInt().toString()
                    tvHomeFeelsLike.text =
                        ("/${((forecast.current.feels_like - 273.15) * 9 / 5 + 32).toInt()}°F")
                }
            }

            tvHomeTempStatus.text = capitalizeWords(forecast.current.weather[0].description)

            when (SharedPreferencesSingleton.readString(
                CommonUtils.KEY_SELECTED_WIND_SPEED, getString(R.string.meter_sec)
            )) {
                getString(R.string.miles_hour) -> {
                    tvHomeWind.text = "${(forecast.current.wind_speed * 2.23694).toInt()} mile/h"
                }

                getString(R.string.meter_sec) -> {
                    tvHomeWind.text = "${forecast.current.wind_speed.toInt()} metre/s"
                }
            }

            tvHomeHumidity.text = ("${forecast.current.humidity} %")
            tvHomeCloud.text = ("${forecast.current.clouds} %")
            tvHomeUv.text = forecast.current.uvi.toString()
            tvHomePressure.text = ("${forecast.current.pressure} hPa")
            tvHomeVisibity.text = ("${forecast.current.visibility} m")
        }
    }

    private fun startUpDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_startup)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val btn_ok = dialog.findViewById<Button>(R.id.btn_startup_ok)

        val rb_GPS = dialog.findViewById<RadioButton>(R.id.rb_gps)
        val rb_Map = dialog.findViewById<RadioButton>(R.id.rb_map)

        rb_GPS.isChecked = true

        btn_ok.setOnClickListener {

            when {
                rb_GPS.isChecked -> {
                    SharedPreferencesSingleton.writeBoolean(KEY_FIRST_STARTUP, false)
                    SharedPreferencesSingleton.writeString(
                        KEY_SELECTED_LOCATION, getString(R.string.gps)
                    )
                    requestForecast()
                }

                rb_Map.isChecked -> {
                    SharedPreferencesSingleton.writeBoolean(KEY_FIRST_STARTUP, false)
                    SharedPreferencesSingleton.writeString(
                        KEY_SELECTED_LOCATION, getString(R.string.map)
                    )
                    val navigationAction = HomeFragmentDirections.actionNavHomeToMapsFragment(
                        MapSelectionType.CURRENT_LOCATION
                    )
                    findNavController().navigate(navigationAction)

                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        if (::locationCallback.isInitialized) fusedClient.removeLocationUpdates(locationCallback)
        if (favLocation != null) (requireActivity() as MainActivity).updateVisiblityOnNavigation(
            View.VISIBLE,
            View.GONE
        )
    }
}