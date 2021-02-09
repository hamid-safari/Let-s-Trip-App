package com.request.trip.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey
    val id: Int?=null,

    val name: String?=null,
    val lat: Double?=null,
    val lon: Double?=null,
    val country: String?=null,
    val population: Int?=null
){

    // For adapter presentations
    override fun toString(): String {
        return name ?: ""
    }
}