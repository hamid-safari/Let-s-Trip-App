package com.request.trip.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.request.trip.profile.User
import com.request.trip.trip.Trip
import com.request.trip.trip.TripLocal
import com.request.trip.utils.TAG
import com.request.trip.utils.TRIPS_COLLECTION
import com.request.trip.utils.USERS_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class SearchViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val trips = MutableLiveData<List<TripLocal?>>()
    private val destinations = ArrayList<String?>()
    val isDeleted = MutableLiveData<TripLocal>()
    private val destinationsFound = MutableLiveData<Boolean>()
    val onDestinationsSet = Transformations.map(destinationsFound) {
        destinations.distinct().take(10) //show only 10 unique destinations in search page
    }

    private var isFirstTime = true

    private suspend fun getUser(trip: Trip): User? {
        return db.collection(USERS_COLLECTION)
            .document(trip.userId!!)
            .get()
            .await()
            .toObject(User::class.java)
    }

    fun getTrips(destination: String) {
        try {
            viewModelScope.launch(IO) {
                val tripLocals = mutableListOf<TripLocal?>()
                val trips = if (destination.isNotEmpty()) {
                    db.collection(TRIPS_COLLECTION)
                        .whereEqualTo(
                            "to_ci", destination.toLowerCase(Locale.getDefault())
                        ) //to_ci: destination lower case letters
                        .whereGreaterThanOrEqualTo("timestamp", System.currentTimeMillis())
                        .get()
                } else {
                    db.collection(TRIPS_COLLECTION)
                        .whereGreaterThanOrEqualTo("timestamp", System.currentTimeMillis())
                        .get()
                }.await()
                    .toObjects(Trip::class.java)

                for (trip in trips) {
                    if (isFirstTime)
                        destinations.add(trip.to)

                    val user = getUser(trip)
                    val tripLocal = TripLocal(trip.id, user, trip.from, trip.to, trip.description, trip.timestamp)
                    tripLocals.add(tripLocal)
                }
                if (isFirstTime)
                    destinationsFound.postValue(true)
                isFirstTime = false
                this@SearchViewModel.trips.postValue(tripLocals)
            }
        } catch (e: Exception) {
            Log.d(TAG, "error: ${e.message}")
        }
    }

    fun deleteTrip(trip: TripLocal) {
        viewModelScope.launch {
            viewModelScope.launch(IO) {
                db.collection(TRIPS_COLLECTION)
                    .document(trip.id!!)
                    .delete()
                    .await()
                isDeleted.postValue(trip)
            }
        }
    }
}