package com.request.trip.login

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.request.trip.profile.User
import com.request.trip.utils.PrefManager
import com.request.trip.utils.USERS_COLLECTION
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginViewModel(val app:Application) : AndroidViewModel(app) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    val isSuccessFull = MutableLiveData<Boolean>()

    fun loginWithGoogle(idToken: String?) {
        // Execute in IO thread with coroutines
        viewModelScope.launch(IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await() //wait to response returns

                val user = auth.currentUser
                val thisUser =
                    User(user?.uid, user?.displayName, user?.email, user?.photoUrl.toString())

                db.collection(USERS_COLLECTION).document(thisUser.uid!!).set(thisUser).await()
                PrefManager.setUID(user?.uid)
                isSuccessFull.postValue(true)
            } catch (e: Exception) {
            }
        }
    }

    fun loginWithFacebook(token: String) {
        viewModelScope.launch(IO) {
            try {
                val credential = FacebookAuthProvider.getCredential(token)
                auth.signInWithCredential(credential).await()

                val user = auth.currentUser
                val thisUser =
                    User(user?.uid, user?.displayName, user?.email, user?.photoUrl.toString())

                db.collection(USERS_COLLECTION)
                    .document(thisUser.uid!!)
                    .set(thisUser)
                    .await()
                PrefManager.setUID(user?.uid)
                isSuccessFull.postValue(true)
            } catch (e: Exception) {
               withContext(Main){
                   //toast must be show in ui thread
                   Toast.makeText(app.applicationContext, e.message.toString(), Toast.LENGTH_SHORT).show()
               }
            }
        }
    }
}