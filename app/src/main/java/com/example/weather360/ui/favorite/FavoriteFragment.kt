package com.example.weather360.ui.favorite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather360.R
import com.example.weather360.databinding.FragmentFavoriteBinding
import com.example.weather360.db.ConcreteLocalSource
import com.example.weather360.model.FavoriteLocation
import com.example.weather360.model.Repository
import com.example.weather360.network.ApiClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerAdapter: FavoriteAdapter

    private lateinit var _viewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val _viewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                ApiClient, ConcreteLocalSource.getInstance(requireContext())
            )
        )

        _viewModel = ViewModelProvider(this, _viewModelFactory)[FavoriteViewModel::class.java]

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fabAddFav.setOnClickListener {
            val navigationAction = FavoriteFragmentDirections.actionNavFavoriteToMapsFragment()
            findNavController().navigate(navigationAction)
        }

        recyclerAdapter = FavoriteAdapter(requireContext(), {

        }, {
            removeLocationDialog(requireContext(), it)
        })

        binding.rvFavorite.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }

        _viewModel.locations.observe(requireActivity()) {
            recyclerAdapter.submitList(it)
        }

        return root
    }

    private fun removeLocationDialog(context: Context, favoriteLocation: FavoriteLocation) {
        MaterialAlertDialogBuilder(context).setTitle("Delete").setMessage("Are you Sure?")
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press
            }.setPositiveButton("Sure") { dialog, which ->
                _viewModel.removeFromFav(favoriteLocation)
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}