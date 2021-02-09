package com.request.trip.chat

import androidx.annotation.Keep

@Keep
data class Chat(
    val chatId: String? = null,
    val memberId: String? = null,
    val lastMessageId: String? = null
)