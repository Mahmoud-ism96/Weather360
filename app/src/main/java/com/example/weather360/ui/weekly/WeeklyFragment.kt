package com.example.weather360.ui.weekly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather360.MainActivity
import com.example.weather360.R
import com.example.weather360.databinding.FragmentWeeklyBinding
import com.example.weather360.util.CommonUtils
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_TEMP_UNIT
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_CELSIUS
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_FAHRENHEIT
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_KELVIN
import com.example.weather360.util.CommonUtils.Companion.WIND_VALUE_METER
import com.example.weather360.util.CommonUtils.Companion.WIND_VALUE_MILE
import com.example.weather360.util.CommonUtils.Companion.capitalizeWords
import com.example.weather360.util.SharedPreferencesSingleton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeeklyFragment : Fragment() {

    private var _binding: FragmentWeeklyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerAdapter: WeeklyAdapter

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).updateVisiblityOnNavigation(View.GONE,View.VISIBLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeeklyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val forecast = WeeklyFragmentArgs.fromBundle(requireArguments()).forecast

        if (forecast != null) {
            binding.apply {
                when (forecast.current.weather[0].icon) {
                    "01d" -> lottieWeekly.setAnimation(R.raw.weather_sunny)
                    "02d" -> lottieWeekly.setAnimation(R.raw.weather_night)
                    "01n" -> lottieWeekly.setAnimation(R.raw.weather_partly_cloudy)
                    "02n" -> lottieWeekly.setAnimation(R.raw.weather_cloudynight)
                    "03d" -> lottieWeekly.setAnimation(R.raw.weather_windy)
                    "03n" -> lottieWeekly.setAnimation(R.raw.weather_windy)
                    "04d" -> lottieWeekly.setAnimation(R.raw.weather_windy)
                    "04n" -> lottieWeekly.setAnimation(R.raw.weather_windy)
                    "09d" -> lottieWeekly.setAnimation(R.raw.weather_partly_shower)
                    "09n" -> lottieWeekly.setAnimation(R.raw.weather_rainynight)
                    "10d" -> lottieWeekly.setAnimation(R.raw.weather_partly_shower)
                    "10n" -> lottieWeekly.setAnimation(R.raw.weather_rainynight)
                    "11d" -> lottieWeekly.setAnimation(R.raw.weather_stormshowersday)
                    "11n" -> lottieWeekly.setAnimation(R.raw.weather_storm)
                    "13d" -> lottieWeekly.setAnimation(R.raw.weather_snow_sunny)
                    "13n" -> lottieWeekly.setAnimation(R.raw.weather_snownight)
                    "50d" -> lottieWeekly.setAnimation(R.raw.weather_foggy)
                    "50n" -> lottieWeekly.setAnimation(R.raw.weather_mist)
                }

                when (SharedPreferencesSingleton.readString(
                    KEY_SELECTED_TEMP_UNIT, TEMP_VALUE_CELSIUS
                )) {
                    TEMP_VALUE_CELSIUS -> {
                        tvWeeklyTemp.text = (forecast.daily[0].temp.day - 273.15).toInt().toString()
                        tvWeeklyFeelsLike.text =
                            ("/${(forecast.daily[0].feels_like.day - 273.15).toInt()} " + getString(R.string.celsius_unit))
                    }

                    TEMP_VALUE_KELVIN-> {
                        tvWeeklyTemp.text = forecast.daily[0].temp.day.toInt().toString()
                        tvWeeklyFeelsLike.text = ("/${forecast.daily[0].feels_like.day.toInt()} " + getString(R.string.kelvin_unit))
                    }

                    TEMP_VALUE_FAHRENHEIT -> {
                        tvWeeklyTemp.text =
                            ((forecast.daily[0].temp.day - 273.15) * 9 / 5 + 32).toInt().toString()
                        tvWeeklyFeelsLike.text =
                            ("/${((forecast.daily[0].feels_like.day - 273.15) * 9 / 5 + 32).toInt()} " + getString(R.string.fahrenheit_unit))
                    }
                }

                tvWeeklyState.text = capitalizeWords(forecast.current.weather[0].description)

                when (SharedPreferencesSingleton.readString(
                    CommonUtils.KEY_SELECTED_WIND_SPEED, WIND_VALUE_METER
                )) {
                    WIND_VALUE_MILE -> {
                        tvWeeklyWind.text =
                            "${(forecast.daily[0].wind_speed * 2.23694).toInt()} " + getString(R.string.mile_h)
                    }

                    WIND_VALUE_METER -> {
                        tvWeeklyWind.text = "${forecast.daily[0].wind_speed.toInt()} " + getString(R.string.metre_s)
                    }
                }

                tvWeeklyHumidity.text = ("${forecast.daily[0].humidity} %")
                tvWeeklyClouds.text = ("${forecast.daily[0].clouds} %")
            }

            recyclerAdapter = WeeklyAdapter(requireContext())

            binding.rvWeekly.apply {
                adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = RecyclerView.VERTICAL
                }
                overScrollMode = View.OVER_SCROLL_NEVER
            }

            val mutableList = forecast.daily.toMutableList()
            mutableList.removeAt(0)

            val currentDate = Date(forecast.daily[0].dt * 1000)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = simpleDateFormat.format(currentDate)
            val todayString = simpleDateFormat.format(Date())
            if (dateString == todayString) {
                mutableList.removeAt(0)
            }

            recyclerAdapter.submitList(mutableList)
        }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).updateVisiblityOnNavigation(View.VISIBLE,View.GONE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}