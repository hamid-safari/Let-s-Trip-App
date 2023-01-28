package com.request.trip.trip

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.request.trip.utils.G
import com.request.trip.R
import com.request.trip.databinding.ItemTripBinding
import com.request.trip.utils.PrefManager
import com.request.trip.utils.setGone
import com.request.trip.utils.setVisible

class TripAdapter(val onTripClickListener: OnTripClickListener) :
    ListAdapter<TripLocal, TripAdapter.TripViewHolder>(
        TripDiffCallback()
    ) {

    private var currentUserUID = PrefManager.getUID()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding =
            ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TripViewHolder(private val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TripLocal) {
            binding.txtNameItemTrip.text = item.user?.name
            binding.txtDescItemTrip.text = G.context.getString(R.string.search_trip_from_to, item.from, item.to)
            Glide.with(binding.root).load(item.user?.avatar).circleCrop()
                .into(binding.imgAvatarItemTrip)

            binding.root.setOnClickListener {
                onTripClickListener.onTripClick(item)
            }

            binding.btnDeleteTrip.setOnClickListener {
                onTripClickListener.onDeleteClick(item)
            }

            //difference between ui of my trips and other trips
            if (currentUserUID.equals(item.user?.uid)) {
                binding.root.setCardBackgroundColor(Color.parseColor("#FAFAFA"))
                binding.root.isClickable = false
                binding.btnDeleteTrip.setVisible()
            } else {
                binding.root.setCardBackgroundColor(Color.WHITE)
                binding.root.isClickable = true
                binding.btnDeleteTrip.setGone()
            }
        }
    }

    fun deleteTrip(trip: TripLocal) {
        val trips = currentList.toMutableList()
        trips.remove(trip)
        submitList(trips)
    }

    override fun submitList(list: List<TripLocal?>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}

class TripDiffCallback : DiffUtil.ItemCallback<TripLocal>() {
    override fun areItemsTheSame(oldItem: TripLocal, newItem: TripLocal): Boolean {
        return oldItem.user == newItem.user
    }

    override fun areContentsTheSame(oldItem: TripLocal, newItem: TripLocal): Boolean {
        return oldItem == newItem
    }
}

interface OnTripClickListener {
    fun onTripClick(trip: TripLocal)
    fun onDeleteClick(trip: TripLocal)
}