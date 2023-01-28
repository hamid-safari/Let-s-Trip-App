package com.request.trip.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.request.trip.*
import com.request.trip.databinding.FragmentChatBinding
import com.request.trip.profile.User
import com.request.trip.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment : Fragment(R.layout.fragment_chat), View.OnClickListener,
    OnMessageClickListener {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val args: ChatFragmentArgs by navArgs()
    lateinit var auth: FirebaseAuth
    private lateinit var targetUser: User
    private var currentUser: FirebaseUser? = null
    private lateinit var chatUID: String
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatBinding.bind(view)

        currentUser = auth.currentUser
        initRecyclerview()
        targetUser = args.user
        binding.txtNameChat.text = targetUser.name
        Glide.with(this).load(targetUser.avatar).circleCrop().into(binding.imgAvatarChat)
        chatUID = createChatUID()
        binding.toolbarChat.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.btnSendMessage.setOnClickListener(this)

        db.collection(CHAT_MESSAGES_COLLECTION)
            .whereEqualTo("chatId", chatUID)
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    showToast("Error: ${e.message}")
                    Log.d(TAG, e.message.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val messages = snapshot.toObjects(Message::class.java)
                    adapter.submitList(messages)
                }
            }
    }

    private fun initRecyclerview() {
        binding.rvChat.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.rvChat.layoutManager = layoutManager
        adapter = MessageAdapter(this, currentUser)
        binding.rvChat.adapter = adapter
    }

    //create same chatUID for two users
    private fun createChatUID(): String {
        val UIDs = arrayListOf(currentUser!!.uid, targetUser.uid!!)
        UIDs.sort()
        return "${UIDs[0]},${UIDs[1]}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnSendMessage -> {
                val text = binding.edtMessage.getValue()
                val time = System.currentTimeMillis()

                val message = Message(chatUID, null, currentUser?.uid!!, time, text)

                val reference = db.collection(CHAT_MESSAGES_COLLECTION).document()
                val messageId = reference.id
                message.messageId = messageId
                val chat = Chat(chatUID, targetUser.uid, messageId)

                db.collection(CHAT_MESSAGES_COLLECTION).document(messageId).set(message)
                val chatLocal = ChatLocal(
                    chatUID,
                    currentUser!!.uid,
                    targetUser.uid,
                    targetUser.name,
                    targetUser.avatar,
                    text,
                    time
                )

                db.collection(LAST_MESSAGE_COLLECTIONS).document(chatUID).set(chatLocal)
                db.collection(USER_CHATS_COLLECTION).document(currentUser!!.uid).set(chat)
                binding.edtMessage.text.clear()
            }
        }
    }

    override fun onMessageClick(message: Message) {}
}