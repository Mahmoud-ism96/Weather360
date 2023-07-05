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
import com.example.weather360.util.CommonUtils
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
        holder.binding.apply {
            when (SharedPreferencesSingleton.readString(
                CommonUtils.KEY_SELECTED_TEMP_UNIT, context.getString(R.string.celsius)
            )) {
                context.getString(R.string.celsius) -> {
                    tvWeeklyItemTemp.text = (currentItem.temp.day - 273.15).toInt().toString()
                    tvWeeklyItemFeelsLike.text = ("/${(currentItem.feels_like.day - 273.15).toInt()}°C")
                }

                context.getString(R.string.kelvin) -> {
                    tvWeeklyItemTemp.text = currentItem.temp.day.toInt().toString()
                    tvWeeklyItemFeelsLike.text = ("/${currentItem.feels_like.day.toInt()} K")
                }

                context.getString(R.string.fahrenheit) -> {
                    tvWeeklyItemTemp.text =
                        ((currentItem.temp.day - 273.15) * 9 / 5 + 32).toInt().toString()
                    tvWeeklyItemFeelsLike.text =
                        ("/${((currentItem.feels_like.day - 273.15) * 9 / 5 + 32).toInt()}°F")
                }
            }

            val description = currentItem.weather[0].description
            val capitalizedDescription =
                description.split(" ").joinToString(" ") { it.capitalize() }

            tvWeeklyItemState.text = capitalizedDescription
            tvWeeklyItemDay.text = "${getDayOfWeek(currentItem.dt.toLong())}"
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