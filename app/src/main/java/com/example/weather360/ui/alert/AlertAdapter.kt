package com.example.weather360.ui.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather360.databinding.AlertItemBinding
import com.example.weather360.model.AlertForecast

class AlertAdapter(val onDelete: (AlertForecast) -> Unit) :
    ListAdapter<AlertForecast, AlertAdapter.AlertViewHolder>(AlertDiffUtil()) {

    private lateinit var binding: AlertItemBinding

    class AlertViewHolder(var binding: AlertItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = AlertItemBinding.inflate(inflater, parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.binding.apply {
            tvAlertItemTime.text = currentItem.time
            tvAlertItemDate.text = currentItem.date
        }

        holder.binding.btnAlertRemove.setOnClickListener{
            onDelete(currentItem)
        }
    }
}

class AlertDiffUtil : DiffUtil.ItemCallback<AlertForecast>() {
    override fun areItemsTheSame(oldItem: AlertForecast, newItem: AlertForecast): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: AlertForecast, newItem: AlertForecast): Boolean {
        return oldItem == newItem
    }

}