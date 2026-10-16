package com.campusconnect.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.campusconnect.app.R
import com.campusconnect.app.data.model.NotificationItem
import com.campusconnect.app.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val onNotificationClicked: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private var items: List<NotificationItem> = emptyList()

    fun submitList(newList: List<NotificationItem>) {
        items = newList
        notifyDataSetChanged()
    }

    inner class NotificationViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.binding.tvNotifTitle.text = item.title
        holder.binding.tvNotifMessage.text = item.message

        if (item.isRead) {
            holder.binding.imgUnreadDot.visibility = View.GONE
            holder.binding.cardNotification.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface_card))
        } else {
            holder.binding.imgUnreadDot.visibility = View.VISIBLE
            holder.binding.cardNotification.setCardBackgroundColor(ContextCompat.getColor(context, R.color.chip_bg))
        }

        holder.itemView.setOnClickListener { onNotificationClicked(item) }
    }

    override fun getItemCount(): Int = items.size
}
