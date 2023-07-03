package com.example.weather360.ui.settings

import android.content.Context
import android.content.SharedPreferences
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

    private lateinit var sharedPref: SharedPreferences


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)!!

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
            sharedPref.getString(KEY_SELECTED_LOCATION, listLocation[0]) ?: listLocation[0]
        selectedTemperatureUnit =
            sharedPref.getString(KEY_SELECTED_TEMP_UNIT, listTemperatureUnit[0])
                ?: listTemperatureUnit[0]
        selectedWindSpeed =
            sharedPref.getString(KEY_SELECTED_WIND_SPEED, listWindSpeed[0]) ?: listWindSpeed[0]
        selectedLanguage =
            sharedPref.getString(KEY_SELECTED_LANGUAGE, listLanguage[0]) ?: listLanguage[0]

        binding.tvSettingsLocationValue.text = selectedLocation
        binding.tvSettingsTempValue.text = selectedTemperatureUnit
        binding.tvSettingsSpeedValue.text = selectedWindSpeed
        binding.tvSettingsLangValue.text = selectedLanguage
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
                saveSelectedItemToPreferences(KEY_SELECTED_LOCATION, selectedLocation)
                if (selectedLocation == getString(R.string.map)) {
                    val navigationAction = SettingsFragmentDirections.actionNavSettingsToMapsFragment(
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
                selectedTemperatureUnit
            ) { selected ->
                selectedTemperatureUnit = selected
                binding.tvSettingsTempValue.text = selectedTemperatureUnit
                saveSelectedItemToPreferences(KEY_SELECTED_TEMP_UNIT, selectedTemperatureUnit)
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
                saveSelectedItemToPreferences(KEY_SELECTED_WIND_SPEED, selectedWindSpeed)
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
                saveSelectedItemToPreferences(KEY_SELECTED_LANGUAGE, selectedLanguage)
            }
        }
    }

    private fun saveSelectedItemToPreferences(key: String, value: String) {
        sharedPref.edit().putString(key, value).apply()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}