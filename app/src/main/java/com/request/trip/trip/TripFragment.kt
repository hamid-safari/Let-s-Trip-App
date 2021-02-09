package com.request.trip.trip

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.request.trip.R
import com.request.trip.databinding.FragmentTripBinding
import com.request.trip.utils.*
import java.util.*

class TripFragment : Fragment(R.layout.fragment_trip), TextWatcher, View.OnClickListener,
    DatePickerDialog.OnDateSetListener {
    private var _binding: FragmentTripBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TripViewModel by viewModels()
    private var type = ""
    private var timeStamp: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTripBinding.bind(view)

        viewModel.queryChanged.observe(viewLifecycleOwner) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it)
            if (type == "FROM")
                binding.edtFrom.setAdapter(adapter)
            else
                binding.edtTo.setAdapter(adapter)
        }

        binding.edtFrom.addTextChangedListener(this)
        binding.edtTo.addTextChangedListener(this)

        binding.edtDateTrip.setOnClickListener(this)
        binding.btnAddTrip.setOnClickListener(this)

        binding.edtDescAddTrip.makeMultilineActionDone()

        viewModel.isAdded.observe(viewLifecycleOwner) {
            if (it) {
                for (child in binding.rootAddTrip.children)
                    if (child is EditText)
                        child.text.clear()
                showToast(getString(R.string.trip_added_successfully))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        type = if (s.hashCode() == binding.edtFrom.text.hashCode()) "FROM" else "TO"
        viewModel.name.value = s.toString()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnAddTrip -> {
                val trip = Trip(
                    userId = getMainActivity().getCurrentUser()?.uid,
                    from = binding.edtFrom.getValue(),
                    to = binding.edtTo.getValue(),
                    to_ci = binding.edtTo.getValue().toLowerCase(),
                    timestamp = timeStamp,
                    description = binding.edtDescAddTrip.getValue()
                )
                viewModel.addTrip(trip)
            }
            R.id.edtDateTrip -> showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val currentDay = getCurrentDate() // select current date of date picker dialog when show it
        DatePickerDialog(requireContext(), this, currentDay.first, currentDay.second, currentDay.third).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = GregorianCalendar(year, month, dayOfMonth)
        timeStamp = date.timeInMillis
        binding.edtDateTrip.setText("$year/${month + 1}/$dayOfMonth")
    }
}