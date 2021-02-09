package com.request.trip.trip

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Trip(
    var id: String? = null,
    var userId: String? = null,
    var from: String? = null,
    var to: String? = null,
    var to_ci: String? = null,
    var timestamp: Long? = null,
    var description: String? = null
):Parcelable