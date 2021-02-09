package com.request.trip.chat

import androidx.annotation.Keep

@Keep
data class Message(
    val chatId: String?=null,
    var messageId: String?=null,
    val sentBy: String?=null,
    val timeStamp: Long?=null,
    val text: String?=null
)