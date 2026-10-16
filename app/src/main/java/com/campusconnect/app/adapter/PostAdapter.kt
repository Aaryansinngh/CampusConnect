package com.campusconnect.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.campusconnect.app.R
import com.campusconnect.app.data.model.Post
import com.campusconnect.app.databinding.ItemPostBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(
    private val onLikeClicked: (Post) -> Unit,
    private val onCommentClicked: (Post) -> Unit,
    private val onSaveClicked: (Post) -> Unit,
    private val onPostClicked: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var posts: List<Post> = emptyList()

    fun submitList(newList: List<Post>) {
        posts = newList
        notifyDataSetChanged()
    }

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val context = holder.itemView.context

        holder.binding.tvClubName.text = post.clubName
        holder.binding.tvPostTitle.text = post.title
        holder.binding.tvPostContent.text = post.content
        holder.binding.tvLikeCount.text = post.likeCount.toString()
        holder.binding.tvCommentCount.text = post.commentCount.toString()

        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        holder.binding.tvPostTime.text = sdf.format(Date(post.createdAt))

        if (post.type == Post.TYPE_ANNOUNCEMENT) {
            holder.binding.tvBadgeAnnouncement.visibility = View.VISIBLE
        } else {
            holder.binding.tvBadgeAnnouncement.visibility = View.GONE
        }

        if (post.imageUrl.isNotBlank()) {
            holder.binding.imgPostMedia.visibility = View.VISIBLE
            Glide.with(context)
                .load(post.imageUrl)
                .placeholder(R.drawable.bg_rounded_card)
                .into(holder.binding.imgPostMedia)
        } else {
            holder.binding.imgPostMedia.visibility = View.GONE
        }

        if (post.clubLogoUrl.isNotBlank()) {
            Glide.with(context)
                .load(post.clubLogoUrl)
                .placeholder(R.drawable.ic_logo)
                .into(holder.binding.imgClubLogo)
        }

        // Like state
        if (post.isLikedByCurrentUser) {
            holder.binding.imgLikeIcon.setImageResource(R.drawable.ic_like_filled)
            holder.binding.imgLikeIcon.setColorFilter(ContextCompat.getColor(context, R.color.accent_rose))
        } else {
            holder.binding.imgLikeIcon.setImageResource(R.drawable.ic_like)
            holder.binding.imgLikeIcon.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary))
        }

        // Save state
        if (post.isSavedByCurrentUser) {
            holder.binding.btnSave.setImageResource(R.drawable.ic_bookmark_filled)
            holder.binding.btnSave.setColorFilter(ContextCompat.getColor(context, R.color.primary))
        } else {
            holder.binding.btnSave.setImageResource(R.drawable.ic_bookmark)
            holder.binding.btnSave.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary))
        }

        holder.binding.btnLike.setOnClickListener { onLikeClicked(post) }
        holder.binding.btnComment.setOnClickListener { onCommentClicked(post) }
        holder.binding.btnSave.setOnClickListener { onSaveClicked(post) }
        holder.itemView.setOnClickListener { onPostClicked(post) }
    }

    override fun getItemCount(): Int = posts.size
}
