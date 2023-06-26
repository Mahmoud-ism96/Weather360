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
import com.example.weather360.util.CommonUtils.Companion.fromUnixToTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeAdapter(private val context: Context) :
    ListAdapter<Hourly, HomeAdapter.ProductViewHolder>(ProductDiffUtil()) {

    private lateinit var binding: HourlyItemBinding

    class ProductViewHolder(var binding: HourlyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = HourlyItemBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = getItem(position)
        holder.binding.apply {
            tvHourlyTemp.text = "${currentProduct.temp.toInt()} F"
            tvHourlyTime.text = "${fromUnixToTime(currentProduct.dt)}"
            Glide.with(context)
                .load("https://openweathermap.org/img/wn/${currentProduct.weather[0].icon}.png")
                .apply(RequestOptions().override(200, 200))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground).into(ivHourlyIcon)

        }
    }
}

class ProductDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }

}