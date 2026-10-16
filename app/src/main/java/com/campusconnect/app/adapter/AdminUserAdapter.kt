package com.campusconnect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.campusconnect.app.data.model.User
import com.campusconnect.app.databinding.ItemAdminUserBinding

class AdminUserAdapter(
    private val onRoleChanged: (User, String) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder>() {

    private var users: List<User> = emptyList()

    fun submitList(newList: List<User>) {
        users = newList
        notifyDataSetChanged()
    }

    inner class AdminUserViewHolder(val binding: ItemAdminUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminUserViewHolder {
        val binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminUserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvUserName.text = user.name
        holder.binding.tvUserEmail.text = user.email
        holder.binding.tvUserRole.text = "Role: ${user.role}"

        holder.binding.btnMakeAdmin.setOnClickListener {
            onRoleChanged(user, User.ROLE_CLUB_ADMIN)
        }
        holder.binding.btnMakeStudent.setOnClickListener {
            onRoleChanged(user, User.ROLE_STUDENT)
        }
    }

    override fun getItemCount(): Int = users.size
}
