package com.theost.tike.ui.adapters.basic

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.databinding.ItemWeekDayBinding
import com.theost.tike.ui.adapters.basic.WeekDaysAdapter.ViewHolder

class WeekDaysAdapter(
    private val weekDays: List<String>
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWeekDayBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding, parent.measuredWidth / weekDays.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(weekDays[position])
    }

    override fun getItemCount(): Int = weekDays.size

    class ViewHolder(
        private val binding: ItemWeekDayBinding,
        private val width: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            with(binding) {
                root.width = width
                dayOfWeek.text = item
            }
        }
    }
}