package com.project.travelbuddy.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.travelbuddy.R
import com.project.travelbuddy.adapter.CountryAdapter
import com.project.travelbuddy.databinding.FragmentExploreBinding
import com.project.travelbuddy.db.TravelBuddyDatabase
import com.project.travelbuddy.repo.CountryRepository
import com.project.travelbuddy.util.Constant
import com.project.travelbuddy.viewmodel.CountryViewModel
import com.project.travelbuddy.viewmodel.CountryViewModelFactory

class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private lateinit var binding: FragmentExploreBinding
    private lateinit var viewModel: CountryViewModel
    private lateinit var adapter: CountryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentExploreBinding.bind(view)

        // Initialize Room Database & ViewModel
        val countryDao = TravelBuddyDatabase.getDatabase(requireContext()).countryDao()
        val repository = CountryRepository(countryDao)
        val viewModelFactory = CountryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CountryViewModel::class.java)

        // Setup RecyclerView
        adapter = CountryAdapter()
        binding.recyclerView.adapter = adapter

        Constant.showLoading(requireContext())

        // Fetch and observe countries
        viewModel.fetchCountriesData()
        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            adapter.submitList(countries)
            Constant.hideLoading()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        })
    }
}
