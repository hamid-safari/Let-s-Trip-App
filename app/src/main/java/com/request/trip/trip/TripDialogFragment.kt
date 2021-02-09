package com.request.trip.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.request.trip.R
import com.request.trip.databinding.FragmentTripDialogBinding
import com.request.trip.notification.Request
import com.request.trip.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore

class TripDialogFragment(val trip: TripLocal) : BottomSheetDialogFragment() {
    private var _binding: FragmentTripDialogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val color = ContextCompat.getColor(requireContext(), R.color.colorSecondaryText)
        Glide.with(this).load(trip.user?.avatar).into(binding.imgAvatarTripDialog)
        binding.txtNameTripDialog.text = trip.user?.name

        val description = trip.description
        binding.txtDescTripDialog.text = getString(R.string.trip_desc, description)
        binding.txtDescTripDialog.changeTextColor(color, 12, binding.txtDescTripDialog.text?.length ?: 12)


        val from = trip.from
        binding.txtFromTripDialog.text = getString(R.string.trip_from, from)
        binding.txtFromTripDialog.changeTextColor(color, 6, binding.txtFromTripDialog.text?.length ?: 6)


        val to = trip.to
        binding.txtToTripDialog.text = getString(R.string.trip_to, to)
        binding.txtToTripDialog.changeTextColor(color, 4, binding.txtToTripDialog.text?.length ?: 4)


        val date = toStrFormat(trip.timestamp) ?: ""
        binding.txtStartDateTripDialog.text = getString(R.string.trip_date, date)
        binding.txtStartDateTripDialog.changeTextColor(color, 6, binding.txtStartDateTripDialog.text.length)

        binding.btnRequestTripDialog.setOnClickListener {
            val request = Request(
                null,
                PrefManager.getUID()!!,
                trip.user?.uid!!,
                trip.mapToTrip(),
                TRIP_REQUEST_REQUESTED,
                System.currentTimeMillis()
            )

            val currentUser = db.collection(USERS_COLLECTION)
                .document(PrefManager.getUID()!!)
                .collection(REQUESTS_COLLECTION)
                .document()

            val friendUser = db.collection(USERS_COLLECTION)
                .document(trip.user?.uid!!)
                .collection(REQUESTS_COLLECTION)
                .document()

            db.runBatch {
                request.id = currentUser.id
                it.set(currentUser, request)

                request.id = friendUser.id
                it.set(friendUser, request)
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast(getString(R.string.request_sent))
                    dismiss()
                } else {
                    showToast(getString(R.string.error))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}