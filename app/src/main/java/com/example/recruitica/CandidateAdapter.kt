package com.example.recruitica


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.bumptech.glide.Glide

class CandidateAdapter(options: FirebaseRecyclerOptions<CandidateData>, private val clickListener: OnCandidateClickListener) :
    FirebaseRecyclerAdapter<CandidateData, CandidateAdapter.MyViewHolder>(options) {

   inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

       fun bind(candidateData: CandidateData, uid: String) {
           Log.d("candidate uid",uid)
           itemView.setOnClickListener {
               clickListener.onCandidateClick(candidateData,uid)
           }
       }
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val imgPhoto: ImageView = itemView.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: CandidateData) {
        val uid = getRef(position).key
        holder.bind(model, uid ?: "")

        holder.nameTextView.text = model.name
        holder.titleTextView.text = model.bio

        Glide.with(holder.itemView.context)
            .load(model.photo)
            .into(holder.imgPhoto)
    }

}