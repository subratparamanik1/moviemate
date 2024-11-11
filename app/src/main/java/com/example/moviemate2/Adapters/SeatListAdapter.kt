package com.example.moviemate2.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moviemate2.models.Seat
import com.example.moviemate2.R
import com.example.moviemate2.databinding.SeatItemBinding

class SeatListAdapter(
    private val seatList: List<Seat>,
    private val context: Context,
    private val selectedSeat: SelectedSeat
) : RecyclerView.Adapter<SeatListAdapter.SeatViewholder>() {

    private val selectedSeatNames = ArrayList<String>()

    class SeatViewholder(val binding: SeatItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewholder {
        return SeatViewholder(
            SeatItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SeatViewholder, position: Int) {
        val seat = seatList[position]
        holder.binding.seat.text = seat.name

        when (seat.status) {
            Seat.SeatStatus.AVAILABLE -> {
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_available)
                holder.binding.seat.setTextColor(context.getColor(R.color.white))
            }
            Seat.SeatStatus.SELECTED -> {
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_selected)
                holder.binding.seat.setTextColor(context.getColor(R.color.black))
            }
            Seat.SeatStatus.UNAVAILABLE -> {
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_unavailable)
                holder.binding.seat.setTextColor(context.getColor(R.color.grey))
            }
        }

        // Handle seat click logic
        holder.binding.seat.setOnClickListener {
            when (seat.status) {
                Seat.SeatStatus.AVAILABLE -> {
                    seat.status = Seat.SeatStatus.SELECTED
                    selectedSeatNames.add(seat.name)
                    notifyItemChanged(position)
                }
                Seat.SeatStatus.SELECTED -> {
                    seat.status = Seat.SeatStatus.AVAILABLE
                    selectedSeatNames.remove(seat.name)
                    notifyItemChanged(position)
                }
                else -> { /* Do nothing for UNAVAILABLE seats */ }
            }
            val selected = selectedSeatNames.joinToString(", ")
            selectedSeat.Return(selected, selectedSeatNames.size)
        }
    }

    override fun getItemCount(): Int = seatList.size

    interface SelectedSeat {
        fun Return(selectedName: String, num: Int)
    }
}
