package com.theost.tike.widgets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.databinding.ItemWeekDayBinding

class WeekDaysAdapter(private val data: Array<String>) :
    RecyclerView.Adapter<WeekDaysAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWeekDayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.layoutParams.width = parent.measuredWidth / 7
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(private val binding: ItemWeekDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            binding.dayOfWeek.text = name
        }
    }

}