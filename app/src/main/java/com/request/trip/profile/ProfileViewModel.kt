package com.request.trip.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.request.trip.utils.USERS_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    val isSuccessfully = MutableLiveData<Boolean>()

    fun changeName(userUID: String?, newName: String) {
        if (userUID == null)
            return
        viewModelScope.launch(IO) {
            try {
                db.collection(USERS_COLLECTION)
                    .document(userUID)
                    .update("name", newName)
                    .await()
                isSuccessfully.postValue(true)
            } catch (e: Exception) {
                isSuccessfully.postValue(false)
            }
        }
    }
}