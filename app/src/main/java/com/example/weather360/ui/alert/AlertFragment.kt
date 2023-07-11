package com.example.weather360.ui.alert

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather360.R
import com.example.weather360.databinding.FragmentAlertBinding
import com.example.weather360.db.ConcreteLocalSource
import com.example.weather360.model.AlertForecast
import com.example.weather360.model.Repository
import com.example.weather360.network.ApiClient
import com.example.weather360.util.AlarmUtils
import com.example.weather360.util.AlarmUtils.cancelAlarm
import com.example.weather360.util.CommonUtils.Companion.readCache
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random


class AlertFragment : Fragment() {

    private var _binding: FragmentAlertBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var _viewModel: AlertViewModel
    private lateinit var alertTime: Calendar
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var recyclerAdapter: AlertAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val _viewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                ApiClient, ConcreteLocalSource.getInstance(requireContext())
            )
        )

        _viewModel = ViewModelProvider(this, _viewModelFactory)[AlertViewModel::class.java]

        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerAdapter = AlertAdapter {
            removeAlertDialog(requireContext(), it)
        }

        binding.rvAlerts.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }

        lifecycleScope.launch {
            _viewModel.alerts.collect { alerts ->
                recyclerAdapter.submitList(alerts)
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val dialog = Dialog(requireContext())

        binding.fabAddAlert.setOnClickListener {

            dialog.setContentView(R.layout.dialog_alert)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)

            val tv_datePicker = dialog.findViewById<TextView>(R.id.tv_alert_date)
            val tv_timePicker = dialog.findViewById<TextView>(R.id.tv_alert_time)
            val btn_ok = dialog.findViewById<TextView>(R.id.btn_alert_ok)
            val btn_cancel = dialog.findViewById<TextView>(R.id.btn_alert_cancel)

            val rb_notification = dialog.findViewById<RadioButton>(R.id.rb_alert_notification)
            val rb_alarm = dialog.findViewById<RadioButton>(R.id.rb_alert_alarm)

            rb_notification.isChecked = true

            var timeSelected = ""
            var dateSelected = ""

            alertTime = Calendar.getInstance()

            tv_datePicker.text = SimpleDateFormat(
                "MMM dd, yyyy", Locale.getDefault()
            ).format(Date(alertTime.timeInMillis))

            tv_timePicker.text = SimpleDateFormat(
                "hh:mm a", Locale.getDefault()
            ).format(alertTime.timeInMillis)

            tv_timePicker.setOnClickListener {

                val hour = alertTime.get(Calendar.HOUR_OF_DAY)
                val minute = alertTime.get(Calendar.MINUTE)

                val picker =
                    MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(hour)
                        .setMinute(minute).setTitleText(getString(R.string.select_appointment_time)).build()

                picker.show(parentFragmentManager, "TIME")

                picker.addOnPositiveButtonClickListener {
                    alertTime.set(Calendar.HOUR_OF_DAY, picker.hour)
                    alertTime.set(Calendar.MINUTE, picker.minute)
                    alertTime.set(Calendar.SECOND, 0)
                    timeSelected =
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(alertTime.time)
                    tv_timePicker.text = timeSelected

                }
            }

            tv_datePicker.setOnClickListener {

                val picker = MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.select_date))
                    .setSelection(alertTime.timeInMillis).build()
                picker.show(parentFragmentManager, "DATE")

                picker.addOnPositiveButtonClickListener {
                    val selectedDate = Date(it)
                    val calendar = Calendar.getInstance()
                    calendar.time = selectedDate

                    calendar.timeZone = TimeZone.getTimeZone("UTC")

                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    val month = calendar.get(Calendar.MONTH)
                    val year = calendar.get(Calendar.YEAR)

                    dateSelected =
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedDate)

                    tv_datePicker.text = dateSelected

                    alertTime.set(Calendar.YEAR, year)
                    alertTime.set(Calendar.MONTH, month)
                    alertTime.set(Calendar.DAY_OF_MONTH, day)
                }
            }

            btn_ok.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val cachedForecast = withContext(Dispatchers.IO) { readCache(requireContext()) }

                    if (cachedForecast != null) {
                        val requestCode = Random.nextInt(Int.MAX_VALUE)

                        if (timeSelected.isBlank()) {
                            timeSelected = SimpleDateFormat(
                                "hh:mm a", Locale.getDefault()
                            ).format(Calendar.getInstance().time)
                        }

                        if (dateSelected.isBlank()) {
                            dateSelected =
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                                    Calendar.getInstance().time
                                )
                        }

                        val alertForecast = AlertForecast(
                            requestCode,
                            timeSelected,
                            dateSelected,
                            cachedForecast.lon,
                            cachedForecast.lat
                        )

                        when {
                            rb_notification.isChecked -> {
                                _viewModel.insertAlert(alertForecast)

                                AlarmUtils.setAlarm(
                                    requireContext(),
                                    alertTime.timeInMillis,
                                    requestCode,
                                    cachedForecast,
                                    "notification"
                                )
                                dialog.dismiss()
                            }

                            rb_alarm.isChecked -> {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                                        requireContext()
                                    )
                                ) {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.please_allow_display_over_other_apps),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                    intent.data =
                                        Uri.parse("package:" + requireActivity().packageName)

                                } else {
                                    _viewModel.insertAlert(alertForecast)

                                    AlarmUtils.setAlarm(
                                        requireContext(),
                                        alertTime.timeInMillis,
                                        requestCode,
                                        cachedForecast,
                                        "alarm"
                                    )
                                    dialog.dismiss()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.no_forecast_data_please_enable_your_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            btn_cancel.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }

        return root
    }

    private fun removeAlertDialog(context: Context, alertForecast: AlertForecast) {
        MaterialAlertDialogBuilder(context).setTitle(getString(R.string.remove_alert)).setMessage(
            getString(
                R.string.remove_alert_dialog_message
            )
        ).setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
            // Respond to neutral button press
        }.setPositiveButton(getString(R.string.sure)) { dialog, which ->
            _viewModel.deleteAlert(alertForecast)
            cancelAlarm(context, alertForecast.id)
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}