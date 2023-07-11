package com.example.weather360.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.weather360.R
import com.example.weather360.databinding.HourlyItemBinding
import com.example.weather360.model.Hourly
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_LANGUAGE
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_TEMP_UNIT
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_AR
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_EN
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_CELSIUS
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_FAHRENHEIT
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_KELVIN
import com.example.weather360.util.CommonUtils.Companion.fromUnixToTime
import com.example.weather360.util.SharedPreferencesSingleton

class HomeAdapter(private val context: Context) :
    ListAdapter<Hourly, HomeAdapter.HomeViewHolder>(HourlyDiffUtil()) {

    private lateinit var binding: HourlyItemBinding

    class HomeViewHolder(var binding: HourlyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = HourlyItemBinding.inflate(inflater, parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.binding.apply {
            val language = when (SharedPreferencesSingleton.readString(KEY_SELECTED_LANGUAGE, LANG_VALUE_EN)) {
                LANG_VALUE_EN -> "en"
                LANG_VALUE_AR -> "ar"
                else -> "en"
            }
            tvHourlyTime.text = fromUnixToTime(currentItem.dt,language)
            when (SharedPreferencesSingleton.readString(
                KEY_SELECTED_TEMP_UNIT, TEMP_VALUE_CELSIUS
            )) {
                TEMP_VALUE_CELSIUS -> {
                    tvHourlyTemp.text =
                        (currentItem.temp - 273.15).toInt().toString() + context.getString(R.string.celsius_unit)
                }

               TEMP_VALUE_KELVIN -> {
                    tvHourlyTemp.text = currentItem.temp.toInt().toString() + " " + context.getString(R.string.kelvin_unit)
                }

                TEMP_VALUE_FAHRENHEIT -> {
                    tvHourlyTemp.text =
                        ((currentItem.temp - 273.15) * 9 / 5 + 32).toInt().toString() + context.getString(R.string.fahrenheit_unit)
                }
            }
            Glide.with(context)
                .load("https://openweathermap.org/img/wn/${currentItem.weather[0].icon}.png")
                .apply(RequestOptions().override(200, 200))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground).into(ivHourlyIcon)
        }


    }
}

class HourlyDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }

}