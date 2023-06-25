package com.example.weather360.ui.weekly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.weather360.R
import com.example.weather360.databinding.FragmentHomeBinding
import com.example.weather360.databinding.FragmentWeeklyBinding
import com.example.weather360.ui.home.HomeViewModel

class WeeklyFragment : Fragment() {

    private var _binding: FragmentWeeklyBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeeklyBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}