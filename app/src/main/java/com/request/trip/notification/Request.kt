package com.request.trip.notification

import android.os.Parcelable
import androidx.annotation.Keep
import com.request.trip.trip.Trip
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Request(
    var id: String? = null,
    val fromUser: String? = null,
    val toUser: String? = null,
    val trip: Trip? = null,
    val status: String? = null,
    val timestamp: Long? = null
) : Parcelable