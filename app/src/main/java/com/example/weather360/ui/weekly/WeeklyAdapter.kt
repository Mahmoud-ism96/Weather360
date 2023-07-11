package com.example.weather360.ui.weekly

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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

        val language = when (SharedPreferencesSingleton.readString(KEY_SELECTED_LANGUAGE, LANG_VALUE_EN)) {
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
                    tvWeeklyItemFeelsLike.text = ("/${(currentItem.feels_like.day - 273.15).toInt()}" + context.getString(R.string.celsius_unit))
                }

                TEMP_VALUE_KELVIN -> {
                    tvWeeklyItemTemp.text = currentItem.temp.day.toInt().toString()
                    tvWeeklyItemFeelsLike.text = ("/${currentItem.feels_like.day.toInt()} "+ context.getString(R.string.kelvin_unit))
                }

                TEMP_VALUE_FAHRENHEIT -> {
                    tvWeeklyItemTemp.text =
                        ((currentItem.temp.day - 273.15) * 9 / 5 + 32).toInt().toString()
                    tvWeeklyItemFeelsLike.text =
                        ("/${((currentItem.feels_like.day - 273.15) * 9 / 5 + 32).toInt()}"+ context.getString(R.string.fahrenheit_unit))
                }
            }

            val description = currentItem.weather[0].description
            val capitalizedDescription =
                description.split(" ").joinToString(" ") { it.capitalize() }

            tvWeeklyItemState.text = capitalizedDescription
            tvWeeklyItemDay.text = "${getDayOfWeek(currentItem.dt,language)}"
            Glide.with(context)
                .load("https://openweathermap.org/img/wn/${currentItem.weather[0].icon}.png")
                .apply(RequestOptions().override(200, 200))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground).into(ivWeeklyItemIcon)
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