package com.example.weather360.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather360.databinding.FragmentHomeBinding
import com.example.weather360.db.ConcreteLocalSource
import com.example.weather360.model.FavoriteLocation
import com.example.weather360.model.Forecast
import com.example.weather360.model.Repository
import com.example.weather360.network.ApiClient
import com.example.weather360.network.ApiStatus
import com.example.weather360.ui.weekly.WeeklyFragmentArgs
import com.example.weather360.util.CommonUtils.Companion.capitalizeWords
import com.example.weather360.util.CommonUtils.Companion.fromUnixToString
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var _viewModel: HomeViewModel
    private lateinit var recyclerAdapter: HomeAdapter

    private var favLocation : FavoriteLocation? = null

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

        if(favLocation != null)
            Log.i("TAG", "onCreateView: $favLocation")

        favLocation = HomeFragmentArgs.fromBundle(requireArguments()).favLocation

//        if (favLocation) {
//
//            Log.i("TAG", "onCreateView: $favLocation")
//        }

        recyclerAdapter = HomeAdapter(requireContext())

        binding.rvHomeHourly.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }

        lifecycleScope.launch {
            _viewModel.forecast.collect {
                when (it) {
                    is ApiStatus.Success -> {
                        val forcast = it.forecast
                        showData(forcast)

                        binding.homeShowDaysClick.setOnClickListener {
                            val navigationAction =
                                HomeFragmentDirections.actionNavHomeToWeeklyFragment(forcast)
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
        return root
    }

    private fun showData(forecast: Forecast) {
        binding.apply {
            tvCityName.text = forecast.timezone
            tvHomeCurrentDate.text = fromUnixToString(forecast.current.dt)
            tvHomeTemp.text = forecast.current.temp.toInt().toString()
            tvHomeFeelsLike.text = ("/${forecast.current.feels_like.toInt()} F")

            tvHomeTempStatus.text = capitalizeWords(forecast.current.weather[0].description)

            tvHomeWind.text = ("${forecast.current.wind_speed} mile/h")
            tvHomeHumidity.text = ("${forecast.current.humidity} %")
            tvHomeCloud.text = ("${forecast.current.clouds} %")
            tvHomeUv.text = forecast.current.uvi.toString()
            tvHomePressure.text = ("${forecast.current.pressure} hpa")
            tvHomeVisibity.text = ("${forecast.current.visibility} m")

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}