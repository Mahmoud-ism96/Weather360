package com.example.weather360.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weather360.R
import com.example.weather360.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var selectedLocation: String
    private lateinit var selectedTemperatureUnit: String
    private lateinit var selectedWindSpeed: String
    private lateinit var selectedLanguage: String

    private lateinit var listLocation: List<String>
    private lateinit var listTemperatureUnit: List<String>
    private lateinit var listWindSpeed: List<String>
    private lateinit var listLanguage: List<String>


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()

        initOnClick()


        return root
    }


    private fun init() {
        listLocation = listOf(
            getString(R.string.map), getString(R.string.gps)
        )

        listTemperatureUnit = listOf(
            getString(R.string.kelvin), getString(R.string.celsius), getString(R.string.fahrenheit)
        )

        listWindSpeed = listOf(
            getString(R.string.meter_sec), getString(R.string.miles_hour)
        )

        listLanguage = listOf(
            getString(R.string.english), getString(R.string.arabic)
        )

        if (!::selectedLocation.isInitialized) selectedLocation = listLocation[0]
        if (!::selectedTemperatureUnit.isInitialized) selectedTemperatureUnit =
            listTemperatureUnit[0]
        if (!::selectedWindSpeed.isInitialized) selectedWindSpeed = listWindSpeed[0]
        if (!::selectedLanguage.isInitialized) selectedLanguage = listLanguage[0]
    }

    private fun initOnClick() {
        binding.settingsLocationClick.setOnClickListener {
            dialogBuilder(
                getString(R.string.location),
                R.drawable.outline_location_on_black_24,
                listLocation,
                selectedLocation
            ) { selected ->
                selectedLocation = selected
                binding.tvSettingsLocationValue.text = selectedLocation
            }
        }


        binding.settingsTempClick.setOnClickListener {
            dialogBuilder(
                getString(R.string.temperature),
                R.drawable.outline_thermostat_black_24,
                listTemperatureUnit,
                selectedTemperatureUnit
            ) { selected ->
                selectedTemperatureUnit = selected
                binding.tvSettingsTempValue.text = selectedTemperatureUnit
            }
        }

        binding.settingsSpeedClick.setOnClickListener {
            dialogBuilder(
                getString(R.string.wind_speed_unit),
                R.drawable.outline_air_black_24,
                listWindSpeed,
                selectedWindSpeed
            ) { selected ->
                selectedWindSpeed = selected
                binding.tvSettingsSpeedValue.text = selectedWindSpeed
            }
        }

        binding.settingsLangClick.setOnClickListener {
            dialogBuilder(
                getString(R.string.language),
                R.drawable.outline_language_black_24,
                listLanguage,
                selectedLanguage
            ) { selected ->
                selectedLanguage = selected
                binding.tvSettingsLangValue.text = selectedLanguage
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dialogBuilder(
        title: String,
        icon: Int,
        stringList: List<String>,
        selected: String,
        onItemSelected: (String) -> Unit
    ) {
        val builder = MaterialAlertDialogBuilder(requireView().context)
        builder.setIcon(icon)
        builder.setTitle(title)
        builder.setSingleChoiceItems(
            stringList.toTypedArray(), stringList.indexOf(selected)
        ) { dialog, which ->
            val selectedItem = stringList[which]
            onItemSelected(selectedItem)
            dialog.dismiss()
        }
        builder.show()
    }
}