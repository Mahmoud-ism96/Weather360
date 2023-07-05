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
import com.example.weather360.util.CommonUtils
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
            tvHourlyTime.text = fromUnixToTime(currentItem.dt)
            when (SharedPreferencesSingleton.readString(
                CommonUtils.KEY_SELECTED_TEMP_UNIT, context.getString(R.string.celsius)
            )) {
                context.getString(R.string.celsius) -> {
                    tvHourlyTemp.text =
                        (currentItem.temp - 273.15).toInt().toString() + "°C"
                }

                context.getString(R.string.kelvin) -> {
                    tvHourlyTemp.text = currentItem.temp.toInt().toString() + " K"
                }

                context.getString(R.string.fahrenheit) -> {
                    tvHourlyTemp.text =
                        ((currentItem.temp - 273.15) * 9 / 5 + 32).toInt().toString() + "°F"
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