package com.request.trip.trip

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.request.trip.db.AppDatabase
import com.request.trip.db.CityDao
import com.request.trip.utils.TRIPS_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TripViewModel : ViewModel() {
    private val localDb = AppDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var cityDao: CityDao? = null
    var name = MutableLiveData<String>()
    var isAdded = MutableLiveData<Boolean>()

    //triggered when name changed
    val queryChanged = Transformations.switchMap(name) {
        cityDao?.searchCities("$it%") // add percent symbol to end of name to use in LIKE query
    }

    init {
        cityDao = localDb.cityDao()
    }


    fun addTrip(trip: Trip) {
        viewModelScope.launch(IO) {
            try {
                val tripId = db.collection(TRIPS_COLLECTION)
                    .document()
                    .id // get trip id from firebase database

                trip.id = tripId

                db.collection(TRIPS_COLLECTION)
                    .document(tripId)
                    .set(trip)
                    .await()
                isAdded.postValue(true)
            } catch (e: Exception) {
            }
        }
    }
}