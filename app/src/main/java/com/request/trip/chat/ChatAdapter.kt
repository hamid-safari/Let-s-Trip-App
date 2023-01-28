package com.request.trip.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.request.trip.profile.User
import com.request.trip.databinding.ItemChatBinding
import com.request.trip.utils.toStrFormat

class ChatAdapter(val onChatClickListener: OnChatClickListener) :
    ListAdapter<ChatLocal, ChatAdapter.ChatViewHolder>(
        ChatDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding =
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }

    inner class ChatViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatLocal) {
            binding.txtNameItemChat.text = item.text
            binding.txtDate.text = toStrFormat(item.time, true)
            Glide.with(binding.root).load(item.avatar).circleCrop().into(binding.imgAvatarItemChat)

            binding.root.setOnClickListener {
                val user = User(item.memberId, item.memberName, null, item.avatar)
                onChatClickListener.onChatClick(user)
            }
        }
    }

    override fun submitList(list: MutableList<ChatLocal?>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

class ChatDiffCallback : DiffUtil.ItemCallback<ChatLocal>() {
    override fun areItemsTheSame(oldItem: ChatLocal, newItem: ChatLocal): Boolean {
        return oldItem.chatId == newItem.chatId
    }

    override fun areContentsTheSame(oldItem: ChatLocal, newItem: ChatLocal): Boolean {
        return oldItem == newItem
    }
}

interface OnChatClickListener {
    fun onChatClick(user: User)
}