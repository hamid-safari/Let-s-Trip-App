package com.request.trip.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.request.trip.*
import com.request.trip.databinding.FragmentNotificationBinding
import com.request.trip.profile.User
import com.request.trip.utils.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NotificationFragment : Fragment(R.layout.fragment_notification), OnNotificationClickListener {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: NotificationAdapter
    private var listener: ListenerRegistration? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotificationBinding.bind(view)

        binding.rvNotification.setHasFixedSize(true)
        adapter = NotificationAdapter(this)
        binding.rvNotification.adapter = adapter


        listener = db.collection(USERS_COLLECTION)
            .document(PrefManager.getUID()!!)
            .collection(REQUESTS_COLLECTION)
            .whereEqualTo("toUser", PrefManager.getUID())
            .addSnapshotListener { value, error ->
                val requests = value?.toObjects(Request::class.java) as List<Request>
                val requestsLocal = ArrayList<RequestLocal>()
                for (request in requests) {
                    val userId = request.fromUser!!
                    db.collection(USERS_COLLECTION)
                        .document(userId)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val fromUser = task.result?.toObject(User::class.java)
                                val toUser = getMainActivity().getCurrentUser()
                                requestsLocal.add(
                                    RequestLocal(
                                        request.id,
                                        fromUser,
                                        toUser,
                                        request.trip,
                                        TRIP_REQUEST_REQUESTED,
                                        request.timestamp
                                    )
                                )
                                adapter.submitList(requestsLocal)
                            }
                        }
                }

                if (requests.isEmpty())
                    binding.emptyViewNotification.setVisible()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onChatClick(user: User?) {
        val action = NotificationFragmentDirections.actionNotificationToChat(user!!)
        findNavController().navigate(action)
    }

    override fun onRejectClick(request: RequestLocal) {
        // delete from fireStore database
        db.collection(USERS_COLLECTION)
            .document(PrefManager.getUID()!!)
            .collection(REQUESTS_COLLECTION)
            .document(request.id!!)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    adapter.deleteNotification(request)
            }
    }

    override fun onPause() {
        super.onPause()

        //remove callback when no need to it
        if (listener != null) {
            listener?.remove()
            listener = null
        }
    }
}