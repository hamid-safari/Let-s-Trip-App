package com.request.trip.utils

import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object PrefManager {
    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(G.context)

    fun getUID() = prefs.getString("UID", "")
    fun setUID(value: String?) = prefs.edit().putString("UID", value).apply()

    fun saveLocale(locale: String) {
        prefs.edit().putString(LOCALE_KEY, locale).apply()
    }

    fun getLocale() = prefs.getString(LOCALE_KEY, LOCALE_ENGLISH)!!

    fun isLocaleChanges(locale: String) = (locale != getLocale())
}