package com.dicoding.picodiploma.fundamentalintermediate.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.fundamentalintermediate.data.response.StoryItem
import com.fundamentalintermediate.R

class StoryAdapter(var onItemClick: (StoryItem) -> Unit) : PagingDataAdapter<StoryItem, StoryAdapter.StoryViewHolder>(
    StoryDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { storyItem ->
            holder.tvName.text = storyItem.name
            holder.tvDescription.text = storyItem.description
            Glide.with(holder.itemView.context)
                .load(storyItem.photoUrl)
                .placeholder(R.drawable.ic_placeholder_photo)
                .into(holder.ivPhoto)
            holder.itemView.setOnClickListener {
                onItemClick(storyItem)
            }
        }
    }

    inner class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_item_name)
        val ivPhoto: ImageView = view.findViewById(R.id.iv_item_photo)
        val tvDescription: TextView = view.findViewById(R.id.tv_description)
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<StoryItem>() {
        override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
            return oldItem == newItem
        }
    }
}