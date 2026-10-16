package com.campusconnect.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.campusconnect.app.R
import com.campusconnect.app.data.model.Club
import com.campusconnect.app.databinding.ItemClubBinding

class ClubAdapter(
    private val onFollowClicked: (Club) -> Unit,
    private val onClubClicked: (Club) -> Unit
) : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    private var clubs: List<Club> = emptyList()

    fun submitList(newList: List<Club>) {
        clubs = newList
        notifyDataSetChanged()
    }

    inner class ClubViewHolder(val binding: ItemClubBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val binding = ItemClubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = clubs[position]
        val context = holder.itemView.context

        holder.binding.tvClubName.text = club.name
        holder.binding.tvClubCategory.text = club.category
        holder.binding.tvClubDescription.text = club.description
        holder.binding.tvFollowerCount.text = "${club.followerCount} Followers"

        if (club.logoUrl.isNotBlank()) {
            Glide.with(context)
                .load(club.logoUrl)
                .placeholder(R.drawable.ic_logo)
                .into(holder.binding.imgClubLogo)
        }

        if (club.followedByCurrentUser) {
            holder.binding.btnFollow.text = context.getString(R.string.following)
            holder.binding.btnFollow.setBackgroundColor(ContextCompat.getColor(context, R.color.chip_bg))
            holder.binding.btnFollow.setTextColor(ContextCompat.getColor(context, R.color.primary))
        } else {
            holder.binding.btnFollow.text = context.getString(R.string.follow)
            holder.binding.btnFollow.setBackgroundColor(ContextCompat.getColor(context, R.color.primary))
            holder.binding.btnFollow.setTextColor(ContextCompat.getColor(context, R.color.on_primary))
        }

        holder.binding.btnFollow.setOnClickListener { onFollowClicked(club) }
        holder.itemView.setOnClickListener { onClubClicked(club) }
    }

    override fun getItemCount(): Int = clubs.size
}
