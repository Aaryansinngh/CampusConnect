package com.campusconnect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.campusconnect.app.data.model.Registration
import com.campusconnect.app.databinding.ItemRegistrationBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrationAdapter : RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder>() {

    private var items: List<Registration> = emptyList()

    fun submitList(newList: List<Registration>) {
        items = newList
        notifyDataSetChanged()
    }

    inner class RegistrationViewHolder(val binding: ItemRegistrationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistrationViewHolder {
        val binding = ItemRegistrationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RegistrationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegistrationViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvStudentName.text = item.userName
        holder.binding.tvStudentEmail.text = item.userEmail
        holder.binding.tvStudentId.text = item.studentId.ifEmpty { "REG-${position + 101}" }

        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        holder.binding.tvRegisteredTime.text = "Registered: ${sdf.format(Date(item.registeredAt))}"
    }

    override fun getItemCount(): Int = items.size
}
