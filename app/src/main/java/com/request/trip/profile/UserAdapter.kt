package com.request.trip.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.request.trip.databinding.ItemUserBinding

class UserAdapter(val onUserClickListener: OnUserClickListener) :
    ListAdapter<User, UserAdapter.UserViewHolder>(
        UserDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User) {
            binding.txtNameItemUser.text = item.name
            Glide.with(binding.root).load(item.avatar).circleCrop().into(binding.imgAvatarItemUser)

            binding.root.setOnClickListener {
                onUserClickListener.onUserClick(item)
            }
        }
    }

    override fun submitList(list: MutableList<User?>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}

interface OnUserClickListener {
    fun onUserClick(user: User)
}