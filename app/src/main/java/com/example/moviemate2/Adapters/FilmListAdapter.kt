package com.example.moviemate2.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.moviemate2.Activities.DetailActivity
import com.example.moviemate2.Domain.ListFilm
import com.example.moviemate2.R

class FilmListAdapter(private val items: ListFilm) : RecyclerView.Adapter<FilmListAdapter.ViewHolder>() {

    private lateinit var context: Context

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_film, parent, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        holder.titleTxt.text = items.data[position].title
        val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(30))

        Glide.with(context)
            .load(items.data[position].poster)
            .apply(requestOptions)
            .into(holder.pic)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("id", items.data[position].id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.data.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        val pic: ImageView = itemView.findViewById(R.id.pic)
    }
}
