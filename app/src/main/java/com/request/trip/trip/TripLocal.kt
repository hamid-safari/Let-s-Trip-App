package com.request.trip.trip

import com.request.trip.profile.User

data class TripLocal(
    var id: String? = null,
    var user: User? = null,
    var from: String? = null,
    var to: String? = null,
    var description: String? = null,
    var timestamp: Long? = null
)