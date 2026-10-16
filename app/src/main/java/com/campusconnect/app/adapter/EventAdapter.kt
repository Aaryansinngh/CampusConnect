package com.campusconnect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.campusconnect.app.R
import com.campusconnect.app.data.model.Event
import com.campusconnect.app.databinding.ItemEventBinding

class EventAdapter(
    private val onEventClicked: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var events: List<Event> = emptyList()

    fun submitList(newList: List<Event>) {
        events = newList
        notifyDataSetChanged()
    }

    inner class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        val context = holder.itemView.context

        holder.binding.tvEventClub.text = event.clubName
        holder.binding.tvEventTitle.text = event.title
        holder.binding.tvEventDate.text = "${event.date} • ${event.startTime}"
        holder.binding.tvEventLocation.text = event.location

        if (event.imageUrl.isNotBlank()) {
            Glide.with(context)
                .load(event.imageUrl)
                .placeholder(R.drawable.bg_rounded_card)
                .into(holder.binding.imgEventPoster)
        }

        holder.itemView.setOnClickListener { onEventClicked(event) }
    }

    override fun getItemCount(): Int = events.size
}
