package com.example.weather360.ui.weekly

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather360.R
import com.example.weather360.databinding.WeeklyItemBinding
import com.example.weather360.model.Daily
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_LANGUAGE
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_TEMP_UNIT
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_AR
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_EN
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_CELSIUS
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_FAHRENHEIT
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_KELVIN
import com.example.weather360.util.CommonUtils.Companion.getDayOfWeek
import com.example.weather360.util.SharedPreferencesSingleton

class WeeklyAdapter(private val context: Context) :
    ListAdapter<Daily, WeeklyAdapter.WeeklyViewHolder>(ProductDiffUtil()) {

    private lateinit var binding: WeeklyItemBinding

    class WeeklyViewHolder(var binding: WeeklyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = WeeklyItemBinding.inflate(inflater, parent, false)
        return WeeklyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyViewHolder, position: Int) {
        val currentItem = getItem(position)

        val language =
            when (SharedPreferencesSingleton.readString(KEY_SELECTED_LANGUAGE, LANG_VALUE_EN)) {
                LANG_VALUE_EN -> "en"
                LANG_VALUE_AR -> "ar"
                else -> "en"
            }

        holder.binding.apply {
            when (SharedPreferencesSingleton.readString(
                KEY_SELECTED_TEMP_UNIT, TEMP_VALUE_CELSIUS
            )) {
                TEMP_VALUE_CELSIUS -> {
                    tvWeeklyItemTemp.text = (currentItem.temp.day - 273.15).toInt().toString()
                    tvWeeklyItemFeelsLike.text =
                        ("/${(currentItem.feels_like.day - 273.15).toInt()}" + context.getString(R.string.celsius_unit))
                }

                TEMP_VALUE_KELVIN -> {
                    tvWeeklyItemTemp.text = currentItem.temp.day.toInt().toString()
                    tvWeeklyItemFeelsLike.text =
                        ("/${currentItem.feels_like.day.toInt()} " + context.getString(R.string.kelvin_unit))
                }

                TEMP_VALUE_FAHRENHEIT -> {
                    tvWeeklyItemTemp.text =
                        ((currentItem.temp.day - 273.15) * 9 / 5 + 32).toInt().toString()
                    tvWeeklyItemFeelsLike.text =
                        ("/${((currentItem.feels_like.day - 273.15) * 9 / 5 + 32).toInt()}" + context.getString(
                            R.string.fahrenheit_unit
                        ))
                }
            }

            val description = currentItem.weather[0].description
            val capitalizedDescription =
                description.split(" ").joinToString(" ") { it.capitalize() }

            tvWeeklyItemState.text = capitalizedDescription
            tvWeeklyItemDay.text = "${getDayOfWeek(currentItem.dt, language)}"


            val icon = currentItem.weather[0].icon
            val imageView = ivWeeklyItemIcon

            when (icon) {
                "01d" -> imageView.setImageResource(R.drawable.weather_01d)
                "02d" -> imageView.setImageResource(R.drawable.weather_02d)
                "01n" -> imageView.setImageResource(R.drawable.weather_01n)
                "02n" -> imageView.setImageResource(R.drawable.weather_02n)
                "03d" -> imageView.setImageResource(R.drawable.weather_03d)
                "03n" -> imageView.setImageResource(R.drawable.weather_03n)
                "04d" -> imageView.setImageResource(R.drawable.weather_04d)
                "04n" -> imageView.setImageResource(R.drawable.weather_04n)
                "09d" -> imageView.setImageResource(R.drawable.weather_09d)
                "09n" -> imageView.setImageResource(R.drawable.weather_09n)
                "10d" -> imageView.setImageResource(R.drawable.weather_10d)
                "10n" -> imageView.setImageResource(R.drawable.weather_10n)
                "11d" -> imageView.setImageResource(R.drawable.weather_11d)
                "11n" -> imageView.setImageResource(R.drawable.weather_11n)
                "13d" -> imageView.setImageResource(R.drawable.weather_13d)
                "13n" -> imageView.setImageResource(R.drawable.weather_13n)
                "50d" -> imageView.setImageResource(R.drawable.weather_50d)
                "50n" -> imageView.setImageResource(R.drawable.weather_50n)
            }
        }
    }
}

class ProductDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }

}