package com.request.trip.profile

import android.os.Parcelable
import androidx.annotation.Keep
import com.request.trip.notification.Request
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class User(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var avatar: String? = null,
    var requests: List<Request>? = null,
) : Parcelable