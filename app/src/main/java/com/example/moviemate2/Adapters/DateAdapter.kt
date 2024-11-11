package com.example.moviemate2.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moviemate2.R
import com.example.moviemate2.databinding.ItemDateBinding
import com.example.moviemate2.databinding.ItemTimeBinding

class DateAdapter(private val timeSlots: List<String>) :
    RecyclerView.Adapter<DateAdapter.TimeViewholder>() {
    private var selectedPosition = -1
    private var lastSelectedPosition = -1


    inner class TimeViewholder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: String) {
            val dateParts = date.split("/")

            // Check if the date has the expected number of parts
            if (dateParts.size == 3) {
                val day = dateParts[0]
                val month = dateParts[1]
                val year = dateParts[2]

                // Check if all parts are valid before setting text
                if (day.isNotBlank() && month.isNotBlank() && year.isNotBlank()) {
                    binding.dayTxt.text = day
                    binding.datMonthTxt.text = "$month $year"
                } else {
                    // Handle cases where day/month/year might be empty
                    binding.dayTxt.text = "-"
                    binding.datMonthTxt.text = "-"
                }
            } else {
                // Handle cases where the date format is incorrect
                binding.dayTxt.text = "-"
                binding.datMonthTxt.text = "-"
            }

            // The rest of your selection logic
            if (selectedPosition == position) {
                binding.mailLayout.setBackgroundResource(R.drawable.white_bg)
                binding.dayTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                binding.datMonthTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            } else {
                binding.mailLayout.setBackgroundResource(R.drawable.light_black_bg)
                binding.dayTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                binding.datMonthTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            }

            // Set the click listener
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    lastSelectedPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(lastSelectedPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateAdapter.TimeViewholder {
        return TimeViewholder(
            ItemDateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DateAdapter.TimeViewholder, position: Int) {
       holder.bind(timeSlots[position])
    }

    override fun getItemCount(): Int = timeSlots.size
}