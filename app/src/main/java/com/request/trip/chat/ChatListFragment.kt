package com.request.trip.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.request.trip.*
import com.request.trip.databinding.FragmentChatListBinding
import com.request.trip.profile.User
import com.request.trip.utils.LAST_MESSAGE_COLLECTIONS
import com.request.trip.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.request.trip.utils.setVisible

class ChatListFragment : Fragment(R.layout.fragment_chat_list), OnChatClickListener {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatListBinding.bind(view)

        binding.rvUsers.setHasFixedSize(true)
        val adapter = ChatAdapter(this)
        binding.rvUsers.adapter = adapter
        binding.rvUsers.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                RecyclerView.VERTICAL
            )
        )
        val user = FirebaseAuth.getInstance().currentUser

        listener = db.collection(LAST_MESSAGE_COLLECTIONS)
            .whereEqualTo("userId", user?.uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    showToast("Error: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val chatLocal = snapshot.toObjects(ChatLocal::class.java)
                    adapter.submitList(chatLocal)
                } else
                    binding.emptyViewChatList.setVisible()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener?.remove()
        _binding = null
    }

    override fun onChatClick(user: User) {
        val action = ChatListFragmentDirections.actionChatListToChat(user)
        findNavController().navigate(action)
    }
}