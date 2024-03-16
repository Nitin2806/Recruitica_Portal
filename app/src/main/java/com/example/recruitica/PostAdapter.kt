package com.example.recruitica

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private var postList: List<PostData>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = postList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
    fun updateData(newList: List<PostData>) {
        postList = newList
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageImageView)
        private val likesView: TextView = itemView.findViewById(R.id.likesTextView)

        fun bind(post: PostData) {

            val description = post.description ?: ""
            val title= post.title ?: ""
            val image= post.imageURL ?: ""
            val likes = post.likes ?: ""
            titleTextView.text= title
            likesView.text= likes.toString()
            contentTextView.text = description
        }
    }
}
