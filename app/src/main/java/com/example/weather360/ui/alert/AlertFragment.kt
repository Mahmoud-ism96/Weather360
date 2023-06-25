package com.example.weather360.ui.alert

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather360.R
import com.example.weather360.databinding.FragmentAlertBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AlertFragment : Fragment() {

    private var _binding: FragmentAlertBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val alertViewModel = ViewModelProvider(this).get(AlertViewModel::class.java)

        _binding = FragmentAlertBinding.inflate(inflater, container, false)
        val root: View = binding.root

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


            tv_timePicker.setOnClickListener {
                val picker =
                    MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(12)
                        .setMinute(10).setTitleText("Select Appointment time").build()

                picker.show(parentFragmentManager, "TIME")

                picker.addOnPositiveButtonClickListener {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
                    calendar.set(Calendar.MINUTE, picker.minute)
                    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val timeSelected = format.format(calendar.time)
                    tv_timePicker.text = timeSelected
                }
            }

            tv_datePicker.setOnClickListener {
                val picker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
                picker.show(parentFragmentManager, "DATE")

                picker.addOnPositiveButtonClickListener {
                    val dateSelected =
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                    tv_datePicker.text = dateSelected
                }


            }

            btn_ok.setOnClickListener {



                dialog.dismiss()
            }

            btn_cancel.setOnClickListener { dialog.dismiss() }

            dialog.show()

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}