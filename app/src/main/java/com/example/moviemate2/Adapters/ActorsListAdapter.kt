package com.example.moviemate2.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviemate2.R

class ActorsListAdapter(private val images: List<String>) : RecyclerView.Adapter<ActorsListAdapter.ViewHolder>() {

    private lateinit var context: Context

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_actors, parent, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(images[position])
            .into(holder.pic)
    }

    override fun getItemCount(): Int = images.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pic: ImageView = itemView.findViewById(R.id.itemImages)
    }
}
