package com.example.weather360.ui.weekly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather360.databinding.FragmentWeeklyBinding
import com.example.weather360.model.Forecast
import com.example.weather360.ui.home.HomeAdapter
import com.example.weather360.util.CommonUtils
import com.example.weather360.util.CommonUtils.Companion.capitalizeWords

class WeeklyFragment : Fragment() {

    private var _binding: FragmentWeeklyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerAdapter: WeeklyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeeklyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val forecast = WeeklyFragmentArgs.fromBundle(requireArguments()).forecast

        binding.apply {
            tvWeeklyTemp.text = forecast.daily[0].temp.day.toInt().toString()
            tvWeeklyFeelsLike.text = ("/${forecast.daily[0].feels_like.day.toInt()} F")
            tvWeeklyState.text = capitalizeWords(forecast.current.weather[0].description)
            tvWeeklyWind.text = ("${forecast.daily[0].wind_speed} mile/h")
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
        forecast.daily = mutableList

        recyclerAdapter.submitList(forecast.daily)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}