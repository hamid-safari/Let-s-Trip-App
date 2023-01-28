package com.request.trip.chat

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.request.trip.databinding.ItemMyMessageBinding
import com.request.trip.databinding.ItemOtherMessageBinding
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val VIEW_TYPE_ME = 1
const val VIEW_TYPE_OTHER = 2

class MessageAdapter(
    val onMessageClickListener: OnMessageClickListener,
    val currentUser: FirebaseUser?
) :
    ListAdapter<Message, RecyclerView.ViewHolder>(
        MessageDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ME) {
            val binding =
                ItemMyMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyMessageViewHolder(binding)
        } else {
            val binding =
                ItemOtherMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return OtherMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyMessageViewHolder)
            holder.bind(getItem(position))
        else if (holder is OtherMessageViewHolder)
            holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentUser?.uid.equals(getItem(position).sentBy)) VIEW_TYPE_ME else VIEW_TYPE_OTHER
    }

    inner class MyMessageViewHolder(private val binding: ItemMyMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.txtTextMessage.text = item.text
            binding.txtTimeMessage.text = getTimeFormat(item.timeStamp!!)
            Glide.with(binding.root).load(currentUser?.photoUrl).circleCrop()
                .into(binding.imgAvatarMessage)
            binding.root.setOnClickListener { onMessageClickListener.onMessageClick(item) }
        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemOtherMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.txtTextMessage.text = item.text
            binding.txtTimeMessage.text = getTimeFormat(item.timeStamp!!)
            Glide.with(binding.root).load(ColorDrawable(Color.RED)).circleCrop()
                .into(binding.imgAvatarMessage)
            binding.root.setOnClickListener { onMessageClickListener.onMessageClick(item) }
        }
    }

    private fun getTimeFormat(timeStamp: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        val dateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormatter.format(calendar.time)
    }

    override fun submitList(list: MutableList<Message?>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.timeStamp == newItem.timeStamp
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}

interface OnMessageClickListener {
    fun onMessageClick(message: Message)
}