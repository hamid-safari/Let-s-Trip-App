package com.request.trip.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.request.trip.utils.G
import com.request.trip.utils.G.Companion.context
import com.request.trip.R
import com.request.trip.profile.User
import com.request.trip.databinding.ItemNotificationBinding
import com.request.trip.utils.changeTextColor

class NotificationAdapter(val onNotificationClickListener: OnNotificationClickListener) :
    ListAdapter<RequestLocal, NotificationAdapter.NotificationViewHolder>(
        RequestDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RequestLocal) {
            val trip = item.trip
            binding.txtNameItemNotification.text = item.fromUser?.name
            binding.txtDescItemNotification.text =
                G.context.getString(R.string.request_for_trip_from_to, trip?.from, trip?.to)

            val color = ContextCompat.getColor(context, R.color.colorPrimaryText)
            binding.txtDescItemNotification.changeTextColor(color, 29, 29 + (trip?.from?.length ?: 0))
            binding.txtDescItemNotification.changeTextColor(color, binding.txtDescItemNotification.text.length - (trip?.to?.length ?: 0) -1 , binding.txtDescItemNotification.text.length)

            Glide.with(binding.root).load(item.fromUser?.avatar).circleCrop()
                .into(binding.imgAvatarItemNotification)

            binding.btnRejectItemRequest.setOnClickListener {
                onNotificationClickListener.onRejectClick(item)
            }

            binding.btnChatItemRequest.setOnClickListener {
                onNotificationClickListener.onChatClick(item.fromUser)
            }
        }
    }

    fun deleteNotification(request: RequestLocal) {
        val requests = currentList.toMutableList()
        requests.remove(request)
        submitList(requests)
    }

    override fun submitList(list: List<RequestLocal?>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

class RequestDiffCallback : DiffUtil.ItemCallback<RequestLocal>() {
    override fun areItemsTheSame(oldItem: RequestLocal, newItem: RequestLocal): Boolean {
        return oldItem.trip == newItem.trip
    }

    override fun areContentsTheSame(oldItem: RequestLocal, newItem: RequestLocal): Boolean {
        return oldItem == newItem
    }
}

interface OnNotificationClickListener {
    fun onChatClick(user: User?)
    fun onRejectClick(request: RequestLocal)
}