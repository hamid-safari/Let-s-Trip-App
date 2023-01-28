package com.request.trip.utils

import java.text.SimpleDateFormat
import java.util.*

fun toStrFormat(timestamp: Long?, containsTime: Boolean? = false): String? {
    if (timestamp == null)
        return null
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    val formatter = SimpleDateFormat(if (containsTime == true) "yyyy/MM/dd HH:mm" else "yyyy/MM/dd", Locale.getDefault())
    return formatter.format(calendar.time)
}

fun getLanguageStr(): String {
    return if (PrefManager.getLocale() == LOCALE_ENGLISH) LANGUAGE_ENGLISH else LANGUAGE_GERMAN
}

fun getCurrentDate(): Triple<Int, Int, Int> {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return Triple(year, month, day)
}