package com.request.trip.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.request.trip.R
import com.request.trip.trip.TripDialogFragment
import com.request.trip.databinding.FragmentSearchBinding
import com.request.trip.delete.ConfirmDialogFragment
import com.request.trip.delete.OnDeleteDialogClickListener
import com.request.trip.trip.OnTripClickListener
import com.request.trip.trip.TripAdapter
import com.request.trip.trip.TripLocal
import com.google.android.material.chip.Chip
import com.request.trip.utils.getValue

class SearchFragment : Fragment(R.layout.fragment_search), OnTripClickListener, View.OnClickListener,
    OnDeleteDialogClickListener<Any> {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        binding.btnSearch.setOnClickListener(this)

        binding.rvSearch.setHasFixedSize(true)
        val adapter = TripAdapter(this)
        binding.rvSearch.adapter = adapter

        viewModel.getTrips("") // get all added trips when start fragment
        viewModel.trips.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.isDeleted.observe(viewLifecycleOwner) {
            if (it != null)
                adapter.deleteTrip(it)
        }

        viewModel.onDestinationsSet.observe(viewLifecycleOwner) { destinations ->
            createChip(getString(R.string.all)) //Add one option to show all trips
            for (destination in destinations)
                createChip(destination)
        }
    }

    private fun createChip(destination: String?) {
        val chip = layoutInflater.inflate(R.layout.chip, binding.chipGroup, false) as Chip
        chip.text = destination
        binding.chipGroup.addView(chip)
        chip.setOnClickListener {
            destination?.let { if (destination == "All") viewModel.getTrips("") else viewModel.getTrips(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnSearch -> {
                val keyword = binding.edtSearch.getValue()
                viewModel.getTrips(keyword) // get trips that entered keyword equal to it's destination
            }
        }
    }

    override fun onTripClick(trip: TripLocal) {
        TripDialogFragment(trip).show(childFragmentManager, "TRIP_DIALOG")
    }

    override fun onDeleteClick(trip: TripLocal) {
        ConfirmDialogFragment(trip, this).show(childFragmentManager, "CONFIRM_DIALOG_TAG")
    }

    override fun onSubmit(obj: Any?) {
        if (obj is TripLocal)
            viewModel.deleteTrip(obj)
    }
}