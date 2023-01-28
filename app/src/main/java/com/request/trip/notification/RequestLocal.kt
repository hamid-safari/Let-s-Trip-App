package com.request.trip.notification

import android.os.Parcelable
import com.request.trip.profile.User
import com.request.trip.trip.Trip
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestLocal(
    val id: String?=null,
    val fromUser: User?=null,
    val toUser: User?=null,
    val trip: Trip?=null,
    val status: String?=null,
    val timestamp: Long?=null
):Parcelable