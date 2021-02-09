package com.request.trip.chat

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ChatLocal(
    val chatId: String? = null,
    var userId: String? = null,
    var memberId: String? = null,
    var memberName: String? = null,
    var avatar: String? = null,
    val text: String? = null,
    val time: Long? = null
):Parcelable