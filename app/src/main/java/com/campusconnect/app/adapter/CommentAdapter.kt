package com.campusconnect.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.campusconnect.app.R
import com.campusconnect.app.data.model.Comment
import com.campusconnect.app.databinding.ItemCommentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter(
    private val currentUserId: String,
    private val onDeleteClicked: (Comment) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private var comments: List<Comment> = emptyList()

    fun submitList(newList: List<Comment>) {
        comments = newList
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        val context = holder.itemView.context

        holder.binding.tvUserName.text = comment.userName
        holder.binding.tvCommentText.text = comment.text

        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        holder.binding.tvCommentTime.text = sdf.format(Date(comment.createdAt))

        if (comment.userProfileImage.isNotBlank()) {
            Glide.with(context).load(comment.userProfileImage).placeholder(R.drawable.ic_profile).into(holder.binding.imgUserAvatar)
        }

        if (comment.userId == currentUserId) {
            holder.binding.btnDeleteComment.visibility = View.VISIBLE
            holder.binding.btnDeleteComment.setOnClickListener { onDeleteClicked(comment) }
        } else {
            holder.binding.btnDeleteComment.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = comments.size
}
