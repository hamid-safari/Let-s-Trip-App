package com.request.trip.language

data class Language(
    val title: String,
    val locale: String
) {

    // show title of language inside spinner
    override fun toString(): String {
        return title
    }
}