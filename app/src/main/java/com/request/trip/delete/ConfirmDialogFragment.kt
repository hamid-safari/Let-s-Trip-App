package com.request.trip.delete

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.request.trip.R
import com.request.trip.databinding.FragmentConfirmDialogBinding
import com.request.trip.utils.makeRound
import com.request.trip.utils.setParams

class ConfirmDialogFragment<T> constructor() : DialogFragment(), View.OnClickListener {
    private var _binding: FragmentConfirmDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var OnDeleteDialogClickListener: OnDeleteDialogClickListener<T>
    private var obj: T? = null

    constructor(obj: T?, onDeleteClick: OnDeleteDialogClickListener<T>) : this() {
        this.OnDeleteDialogClickListener = onDeleteClick
        this.obj = obj
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmDialogBinding.inflate(inflater, container, false)
        makeRound() // curve border of dialog
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancelDelete.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)

        binding.txtSureForDelete.text = Html.fromHtml(getString(R.string.sure_for_delete))
    }


    override fun onStart() {
        super.onStart()
        setParams() // resize dialog when show it
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnDelete -> {
                OnDeleteDialogClickListener.onSubmit(obj)
                dismiss()
            }
            R.id.btnCancelDelete -> dismiss()
        }
    }
}

interface OnDeleteDialogClickListener<T> {
    fun onSubmit(obj: T?)
}