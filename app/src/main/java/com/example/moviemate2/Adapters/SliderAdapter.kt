package com.example.moviemate2.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.moviemate2.Domain.SliderItems
import com.example.moviemate2.R


class SliderAdapter(
    private val sliderItems: MutableList<SliderItems>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.slide_item_container, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(sliderItems[position])
        if (position == sliderItems.size - 2){
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageSlide)

        fun setImage(sliderItems: SliderItems) {
            val requestOptions = RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(40))

            Glide.with(itemView.context)
                .load(sliderItems.image)
                .apply(requestOptions)
                .into(imageView)
        }
    }

    private val runnable = Runnable {
        val newItems = sliderItems.toList()
        sliderItems.addAll(newItems)
        notifyDataSetChanged()
    }

}
