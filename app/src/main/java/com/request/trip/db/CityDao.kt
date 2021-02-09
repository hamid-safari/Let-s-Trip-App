package com.request.trip.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface CityDao {

    @Query("SELECT * FROM city WHERE name LIKE :name")
    fun searchCities(name: String): LiveData<List<City>>
}