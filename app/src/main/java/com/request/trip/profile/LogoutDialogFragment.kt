package com.request.trip.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.request.trip.R
import com.request.trip.databinding.FragmentLogoutDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LogoutDialogFragment(val onLogoutClickListener: OnLogoutClickListener) : BottomSheetDialogFragment(),
    View.OnClickListener {
    private var _binding: FragmentLogoutDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLogoutDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogoutSubmit.setOnClickListener(this)
        binding.btnLogoutCancel.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnLogoutSubmit -> {
                onLogoutClickListener.onLogoutClick()
                dismiss()
            }

            R.id.btnLogoutCancel -> dismiss()
        }
    }
}

interface OnLogoutClickListener {
    fun onLogoutClick()
}