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
import android.widget.Toast
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
import com.example.weather360.util.CommonUtils.Companion.KEY_CURRENT_LAT
import com.example.weather360.util.CommonUtils.Companion.KEY_CURRENT_LONG
import com.example.weather360.util.CommonUtils.Companion.KEY_FIRST_STARTUP
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_LANGUAGE
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_LOCATION
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_TEMP_UNIT
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_WIND_SPEED
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_AR
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_EN
import com.example.weather360.util.CommonUtils.Companion.LOC_VALUE_GPS
import com.example.weather360.util.CommonUtils.Companion.LOC_VALUE_MAP
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_CELSIUS
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_FAHRENHEIT
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_KELVIN
import com.example.weather360.util.CommonUtils.Companion.WIND_VALUE_METER
import com.example.weather360.util.CommonUtils.Companion.WIND_VALUE_MILE
import com.example.weather360.util.CommonUtils.Companion.capitalizeWords
import com.example.weather360.util.CommonUtils.Companion.checkConnectivity
import com.example.weather360.util.CommonUtils.Companion.fromUnixToString
import com.example.weather360.util.CommonUtils.Companion.readCache
import com.example.weather360.util.CommonUtils.Companion.writeCache
import com.example.weather360.util.SharedPreferencesSingleton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var _viewModel: HomeViewModel
    private lateinit var recyclerAdapter: HomeAdapter

    private var favLocation: FavoriteLocation? = null

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var language: String

    private var lastLongitude by Delegates.notNull<Double>()
    private var lastLatitude by Delegates.notNull<Double>()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getLastLocation()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_allow_the_location_permissions_to_get_current_location),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (favLocation != null) (requireActivity() as MainActivity).updateVisiblityOnNavigation(
            View.GONE, View.VISIBLE
        )
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

        initRecyclerView()

        initAPIRequest()

        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.btnRetry.setOnClickListener{
            requestForecast()
        }

        if (isFirstStartUp()) {
            startUpDialog()

            SharedPreferencesSingleton.writeString(
                KEY_SELECTED_TEMP_UNIT, TEMP_VALUE_CELSIUS
            )
            SharedPreferencesSingleton.writeString(
                KEY_SELECTED_WIND_SPEED, WIND_VALUE_METER
            )
            SharedPreferencesSingleton.writeString(
                KEY_SELECTED_LANGUAGE, LANG_VALUE_EN
            )

        } else {
            requestForecast()
        }

        return root
    }

    private fun requestForecast() {
        favLocation = HomeFragmentArgs.fromBundle(requireArguments()).favLocation

        language = SharedPreferencesSingleton.readString(KEY_SELECTED_LANGUAGE, LANG_VALUE_EN)

        if (checkConnectivity(requireActivity())) {

            binding.groupLoading.visibility = View.VISIBLE
            binding.groupRetry.visibility = View.GONE

            if (favLocation == null) {
                when (SharedPreferencesSingleton.readString(
                    KEY_SELECTED_LOCATION, LOC_VALUE_GPS
                )) {
                    LOC_VALUE_GPS -> {
                        if (checkLocationPermission()) {
                            getLastLocation()
                        }
                    }

                    LOC_VALUE_MAP -> {
                        val currentLatitude =
                            SharedPreferencesSingleton.readFloat(KEY_CURRENT_LAT, 0.0f).toDouble()
                        val currentLongitude =
                            SharedPreferencesSingleton.readFloat(KEY_CURRENT_LONG, 0.0f).toDouble()
                        _viewModel.getForecast(currentLatitude, currentLongitude, language)
                    }
                }
            } else {
                (requireActivity() as MainActivity).updateVisiblityOnNavigation(
                    View.GONE, View.VISIBLE
                )
                _viewModel.getForecast(favLocation!!.latitude, favLocation!!.longitude, language)
            }
        } else {
            val cachedForecast = readCache(requireContext())
            if (cachedForecast != null) {
                showData(cachedForecast)

                binding.groupLoading.visibility = View.GONE
                binding.groupRetry.visibility = View.GONE
                binding.groupData.visibility = View.VISIBLE

                binding.homeShowDaysClick.setOnClickListener {
                    val navigationAction =
                        HomeFragmentDirections.actionNavHomeToWeeklyFragment(cachedForecast)
                    findNavController().navigate(navigationAction)
                }
            } else {
                binding.groupRetry.visibility = View.VISIBLE

                Toast.makeText(requireContext(),getString(R.string.failed_to_retrieve_data),Toast.LENGTH_SHORT).show()
            }
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
                _viewModel.getForecast(lastLatitude, lastLongitude, language)
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

                        withContext(Dispatchers.Main) {
                            binding.groupLoading.visibility = View.GONE
                            binding.groupData.visibility = View.VISIBLE
                        }

                        if (favLocation == null) writeCache(forecast, requireContext())

                        binding.homeShowDaysClick.setOnClickListener {
                            val navigationAction =
                                HomeFragmentDirections.actionNavHomeToWeeklyFragment(forecast)
                            findNavController().navigate(navigationAction)
                        }

                        recyclerAdapter.submitList(it.forecast.hourly)
                    }

                    is ApiStatus.Failure -> Log.i("Main", "Error: ${it.err}")
                    else -> {

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
        return SharedPreferencesSingleton.readBoolean(KEY_FIRST_STARTUP, true)
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
            tvHomeCurrentDate.text = fromUnixToString(forecast.current.dt, language)

            when (SharedPreferencesSingleton.readString(
                KEY_SELECTED_TEMP_UNIT, TEMP_VALUE_CELSIUS
            )) {
                TEMP_VALUE_CELSIUS -> {
                    tvHomeTemp.text = (forecast.current.temp - 273.15).toInt().toString()
                    tvHomeFeelsLike.text =
                        ("/${(forecast.current.feels_like - 273.15).toInt()}" + getString(
                            R.string.celsius_unit
                        ))
                }

                TEMP_VALUE_KELVIN -> {
                    tvHomeTemp.text = forecast.current.temp.toInt().toString()
                    tvHomeFeelsLike.text =
                        ("/${forecast.current.feels_like.toInt()} " + getString(R.string.kelvin_unit))
                }

                TEMP_VALUE_FAHRENHEIT -> {
                    tvHomeTemp.text =
                        ((forecast.current.temp - 273.15) * 9 / 5 + 32).toInt().toString()
                    tvHomeFeelsLike.text =
                        ("/${((forecast.current.feels_like - 273.15) * 9 / 5 + 32).toInt()}" + getString(
                            R.string.fahrenheit_unit
                        ))
                }
            }

            tvHomeTempStatus.text = capitalizeWords(forecast.current.weather[0].description)

            when (SharedPreferencesSingleton.readString(
                KEY_SELECTED_WIND_SPEED, WIND_VALUE_METER
            )) {
                WIND_VALUE_MILE -> {
                    tvHomeWind.text =
                        "${(forecast.current.wind_speed * 2.23694).toInt()} " + getString(
                            R.string.mile_h
                        )
                }

                WIND_VALUE_METER -> {
                    tvHomeWind.text =
                        "${forecast.current.wind_speed.toInt()} " + getString(R.string.metre_s)
                }
            }

            tvHomeHumidity.text = ("${forecast.current.humidity} %")
            tvHomeCloud.text = ("${forecast.current.clouds} %")
            tvHomeUv.text = forecast.current.uvi.toString()
            tvHomePressure.text = ("${forecast.current.pressure} " + getString(R.string.hpa))
            tvHomeVisibity.text = ("${forecast.current.visibility} " + getString(R.string.meter))
        }


        when (language) {
            LANG_VALUE_EN -> {
                binding.ivHomeArrow.rotationY = 0F
            }

            LANG_VALUE_AR -> {
                binding.ivHomeArrow.rotationY = 180F
            }
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
                        KEY_SELECTED_LOCATION, LOC_VALUE_GPS
                    )
                    requestForecast()
                }

                rb_Map.isChecked -> {
                    SharedPreferencesSingleton.writeBoolean(KEY_FIRST_STARTUP, false)
                    SharedPreferencesSingleton.writeString(
                        KEY_SELECTED_LOCATION, LOC_VALUE_MAP
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
            View.VISIBLE, View.GONE
        )
    }
}