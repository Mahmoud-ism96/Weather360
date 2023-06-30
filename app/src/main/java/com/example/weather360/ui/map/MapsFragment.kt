package com.example.weather360.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather360.R
import com.example.weather360.databinding.FragmentMapsBinding
import com.example.weather360.db.ConcreteLocalSource
import com.example.weather360.model.FavoriteLocation
import com.example.weather360.model.Repository
import com.example.weather360.network.ApiClient
import com.example.weather360.ui.home.HomeViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private var _binding: FragmentMapsBinding? = null
    private lateinit var mapsViewModel: MapsViewModel

    private lateinit var countryName: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val _viewModelFactory = MapsViewModelFactory(
            Repository.getInstance(
                ApiClient, ConcreteLocalSource.getInstance(requireContext())
            )
        )

        mapsViewModel = ViewModelProvider(this, _viewModelFactory).get(MapsViewModel::class.java)

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnSelectLocation.setOnClickListener {
            saveLocation()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        return root
    }

    private fun saveLocation() {
        if (marker != null) {
            val favoriteLocation = FavoriteLocation(
                countryName,
                marker!!.position.longitude,
                marker!!.position.latitude
            )
            mapsViewModel.addToFav(favoriteLocation)
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        setMarkOnClick(mMap)
    }

    private fun setMarkOnClick(map: GoogleMap) {
        map.setOnMapClickListener { latLng ->

            map.clear()

            val snippet = String.format(
                Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f", latLng.latitude, latLng.longitude
            )

            val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            countryName = "Unknown"

            if (!address.isNullOrEmpty()) {
                Log.i("TAG", "setMarkOnClick: $address")
                if (!address[0].adminArea.isNullOrBlank())
                    countryName = address[0].adminArea
                if (!address[0].subAdminArea.isNullOrBlank())
                    countryName += address[0].subAdminArea
                countryName += address[0].countryName
            }
            marker = map.addMarker(
                MarkerOptions().position(latLng).title(countryName).snippet(snippet)
            )

            marker?.showInfoWindow()

            binding.btnSelectLocation.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
