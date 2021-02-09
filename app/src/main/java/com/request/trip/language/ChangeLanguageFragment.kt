package com.request.trip.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.request.trip.R
import com.request.trip.databinding.FragmentChangeLanguageBinding
import com.request.trip.utils.LANGUAGE_ENGLISH
import com.request.trip.utils.LANGUAGE_GERMAN
import com.request.trip.utils.LOCALE_ENGLISH
import com.request.trip.utils.LOCALE_GERMAN
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangeLanguageFragment(val selectedLanguage: Language, val onNewLanguageSetListener: OnNewLanguageSetListener) :
    BottomSheetDialogFragment() {
    private var _binding: FragmentChangeLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChangeLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languages =
            arrayListOf(Language(LANGUAGE_ENGLISH, LOCALE_ENGLISH), Language(LANGUAGE_GERMAN, LOCALE_GERMAN))
        val adapter = ArrayAdapter(requireContext(), R.layout.layout_spinner, languages)
        binding.spinnerLanguages.adapter = adapter

        binding.spinnerLanguages.setSelection(languages.indexOf(selectedLanguage))

        binding.btnSubmitLanguage.setOnClickListener {
            val language = binding.spinnerLanguages.selectedItem as Language
            onNewLanguageSetListener.onNewLanguageSet(language)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface OnNewLanguageSetListener {
    fun onNewLanguageSet(language: Language)
}