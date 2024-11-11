package com.example.moviemate2.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moviemate2.R // Use the correct package for R file
import com.example.moviemate2.databinding.ItemTimeBinding // Correct package for binding

class TimeAdapter(private val timeSlots: List<String>) :
    RecyclerView.Adapter<TimeAdapter.TimeViewholder>() {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    inner class TimeViewholder(private val binding: ItemTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(time: String) {
            // Set the time text
            binding.TextViewTime.text = time

            // Handle background and text color change for selected and unselected states
            if (selectedPosition == adapterPosition) {
                binding.TextViewTime.setBackgroundResource(R.drawable.white_bg)
                binding.TextViewTime.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
            } else {
                binding.TextViewTime.setBackgroundResource(R.drawable.light_black_bg)
                binding.TextViewTime.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
            }

            // Set click listener to update selected item
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    lastSelectedPosition = selectedPosition
                    selectedPosition = position

                    // Notify the adapter of item changes for proper view refresh
                    notifyItemChanged(lastSelectedPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewholder {
        // Inflate the binding
        val binding = ItemTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeViewholder(binding)
    }

    override fun onBindViewHolder(holder: TimeViewholder, position: Int) {
        // Bind the time slot to the ViewHolder
        holder.bind(timeSlots[position])
    }

    override fun getItemCount(): Int = timeSlots.size
}
