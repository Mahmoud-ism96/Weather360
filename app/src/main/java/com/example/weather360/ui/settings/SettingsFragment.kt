package com.example.weather360.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weather360.R
import com.example.weather360.databinding.FragmentSettingsBinding
import com.example.weather360.enums.MapSelectionType
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_LANGUAGE
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_LOCATION
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_TEMP_UNIT
import com.example.weather360.util.CommonUtils.Companion.KEY_SELECTED_WIND_SPEED
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_AR
import com.example.weather360.util.CommonUtils.Companion.LANG_VALUE_EN
import com.example.weather360.util.CommonUtils.Companion.LOC_VALUE_GPS
import com.example.weather360.util.CommonUtils.Companion.LOC_VALUE_MAP
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_CELSIUS
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_FAHRENHEIT
import com.example.weather360.util.CommonUtils.Companion.TEMP_VALUE_KELVIN
import com.example.weather360.util.CommonUtils.Companion.WIND_VALUE_METER
import com.example.weather360.util.CommonUtils.Companion.WIND_VALUE_MILE
import com.example.weather360.util.SharedPreferencesSingleton
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var selectedLocation: String
    private lateinit var selectedTemperatureUnit: String
    private lateinit var selectedWindSpeed: String
    private lateinit var selectedLanguage: String

    private lateinit var selectedLocationItem: String
    private lateinit var selectedTemperatureUnitItem: String
    private lateinit var selectedWindSpeedItem: String
    private lateinit var selectedLanguageItem: String


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
            getString(R.string.gps), getString(R.string.map)
        )

        listTemperatureUnit = listOf(
            getString(R.string.celsius), getString(R.string.kelvin), getString(R.string.fahrenheit)
        )

        listWindSpeed = listOf(
            getString(R.string.meter_sec), getString(R.string.miles_hour)
        )

        listLanguage = listOf(
            getString(R.string.english), getString(R.string.arabic)
        )

        selectedLocation =
            SharedPreferencesSingleton.readString(KEY_SELECTED_LOCATION, LOC_VALUE_GPS)
        selectedTemperatureUnit =
            SharedPreferencesSingleton.readString(KEY_SELECTED_TEMP_UNIT, TEMP_VALUE_CELSIUS)
        selectedWindSpeed =
            SharedPreferencesSingleton.readString(KEY_SELECTED_WIND_SPEED, WIND_VALUE_METER)
        selectedLanguage =
            SharedPreferencesSingleton.readString(KEY_SELECTED_LANGUAGE, LANG_VALUE_EN)

        binding.tvSettingsLocationValue.text = when (selectedLocation) {
            LOC_VALUE_GPS -> {
                selectedLocationItem = getString(R.string.gps)
                getString(R.string.gps)
            }

            else -> {
                selectedLocationItem = getString(R.string.map)
                getString(R.string.map)
            }
        }
        binding.tvSettingsTempValue.text = when (selectedTemperatureUnit) {
            TEMP_VALUE_CELSIUS -> {
                selectedTemperatureUnitItem = getString(R.string.celsius)
                getString(R.string.celsius)
            }

            TEMP_VALUE_KELVIN -> {
                selectedTemperatureUnitItem = getString(R.string.kelvin)
                getString(R.string.kelvin)
            }

            else -> {
                selectedTemperatureUnitItem = getString(R.string.fahrenheit)
                getString(R.string.fahrenheit)
            }
        }
        binding.tvSettingsSpeedValue.text = when (selectedWindSpeed) {
            WIND_VALUE_METER -> {
                selectedWindSpeedItem = getString(R.string.meter_sec)
                getString(R.string.meter_sec)
            }

            else -> {
                selectedWindSpeedItem = getString(R.string.miles_hour)
                getString(R.string.miles_hour)
            }
        }
        binding.tvSettingsLangValue.text = when (selectedLanguage) {
            LANG_VALUE_EN -> {
                selectedLanguageItem = getString(R.string.english)
                getString(R.string.english)
            }

            else -> {
                selectedLanguageItem = getString(R.string.arabic)
                getString(R.string.arabic)
            }
        }
    }

    private fun initOnClick() {
        binding.settingsLocationClick.setOnClickListener {
            dialogBuilder(
                getString(R.string.location),
                R.drawable.outline_location_on_black_24,
                listLocation,
                selectedLocationItem
            ) { selected ->
                when (selected) {
                    getString(R.string.gps) -> selectedLocation = LOC_VALUE_GPS
                    getString(R.string.map) -> selectedLocation = LOC_VALUE_MAP
                }
                binding.tvSettingsLocationValue.text = selected
                selectedLocationItem = selected
                SharedPreferencesSingleton.writeString(KEY_SELECTED_LOCATION, selectedLocation)
                if (selectedLocation == LOC_VALUE_MAP) {
                    val navigationAction =
                        SettingsFragmentDirections.actionNavSettingsToMapsFragment(
                            MapSelectionType.CURRENT_LOCATION
                        )
                    findNavController().navigate(navigationAction)
                }
            }
        }

        binding.settingsTempClick.setOnClickListener {
            dialogBuilder(
                getString(R.string.temperature),
                R.drawable.outline_thermostat_black_24,
                listTemperatureUnit,
                selectedTemperatureUnitItem
            ) { selected ->
                when (selected) {
                    getString(R.string.celsius) -> selectedTemperatureUnit = TEMP_VALUE_CELSIUS
                    getString(R.string.kelvin) -> selectedTemperatureUnit = TEMP_VALUE_KELVIN
                    getString(R.string.fahrenheit) -> selectedTemperatureUnit =
                        TEMP_VALUE_FAHRENHEIT
                }
                binding.tvSettingsTempValue.text = selected
                selectedTemperatureUnitItem = selected
                SharedPreferencesSingleton.writeString(
                    KEY_SELECTED_TEMP_UNIT, selectedTemperatureUnit
                )
            }
        }

        binding.settingsSpeedClick.setOnClickListener {
            dialogBuilder(
                getString(R.string.wind_speed_unit),
                R.drawable.outline_air_black_24,
                listWindSpeed,
                selectedWindSpeedItem
            ) { selected ->
                when (selected) {
                    getString(R.string.meter_sec) -> selectedWindSpeed = WIND_VALUE_METER
                    getString(R.string.miles_hour) -> selectedWindSpeed = WIND_VALUE_MILE
                }
                binding.tvSettingsSpeedValue.text = selected
                selectedWindSpeedItem = selected
                SharedPreferencesSingleton.writeString(KEY_SELECTED_WIND_SPEED, selectedWindSpeed)
            }
        }

        binding.settingsLangClick.setOnClickListener {
            dialogBuilder(
                getString(R.string.language),
                R.drawable.outline_language_black_24,
                listLanguage,
                selectedLanguageItem
            ) { selected ->
                when (selected) {
                    getString(R.string.english) -> selectedLanguage = LANG_VALUE_EN
                    getString(R.string.arabic) -> selectedLanguage = LANG_VALUE_AR
                }
                binding.tvSettingsLangValue.text = selected
                selectedLanguageItem = selected
                SharedPreferencesSingleton.writeString(KEY_SELECTED_LANGUAGE, selectedLanguage)
                restartApplication()
            }
        }
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

    private fun restartApplication() {
        val intent = requireActivity().packageManager.getLaunchIntentForPackage(
            requireActivity().packageName
        )
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        requireActivity().finish()
        if (intent != null) {
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}