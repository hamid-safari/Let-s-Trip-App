package com.request.trip.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.method.KeyListener
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.request.trip.MainActivity
import com.request.trip.trip.Trip
import com.request.trip.trip.TripLocal
import java.util.*

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun Fragment.showToast(message: String?) =
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

fun Fragment.getMainActivity() = requireActivity() as MainActivity

fun EditText.getValue() = text.trim().toString()

fun EditText.makeMultilineActionDone() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setRawInputType(InputType.TYPE_CLASS_TEXT)
}

fun EditText.openSoftKeyboard(context: Context) {
    val imm: InputMethodManager? =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.closeSoftKeyboard(context: Context) {
    val imm: InputMethodManager? =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

private fun EditText.setEndSelection() = setSelection(text.length)

fun EditText.toggleEditable(context: Context, isEditable: Boolean) {
    if (isEditable) {
        keyListener = tag as KeyListener
        requestFocus()
        openSoftKeyboard(context)
        setEndSelection()
    } else {
        closeSoftKeyboard(context)
        keyListener = null
    }
}

fun Fragment.screenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun DialogFragment.makeRound() {
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
}

fun DialogFragment.setParams() {
    val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
    params.width = (screenWidth() * 0.9).toInt()
    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
    dialog!!.window!!.attributes = params as WindowManager.LayoutParams
}

fun TextView.changeTextColor(color: Int, start: Int, end: Int) {
    val spannable: Spannable = SpannableString(text)
    spannable.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    setText(spannable, TextView.BufferType.SPANNABLE)
}

fun TripLocal.mapToTrip(): Trip {
    val trip = Trip()
    trip.id = id
    trip.from = from
    trip.to = to
    trip.to_ci = to?.toLowerCase()
    trip.description = description
    trip.timestamp = timestamp
    trip.userId = user?.uid
    return trip
}

fun Context?.setLocale(newLocale: String): Context? {
    if (this != null) {
        val locale = Locale(newLocale)
        Locale.setDefault(locale)
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        PrefManager.saveLocale(newLocale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            applicationContext.createConfigurationContext(config)
        } else {
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
    return this
}

fun Activity.restartApp() {
    finish()
    startActivity(Intent(this, javaClass))
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}