package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.data.models.ui.ListEvent
import com.theost.tike.databinding.ItemEventBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class EventAdapterDelegate : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as ListEvent)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ListEvent

    class ViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listEvent: ListEvent) {
            binding.eventTitle.text = listEvent.title
            binding.eventDescription.text = listEvent.description
            binding.eventTime.text = listEvent.time
            binding.eventUsers.adapter = BaseAdapter().apply {
                addDelegate(ParticipantAvatarAdapterDelegate())
                submitList(listEvent.participants)
            }
        }

    }

}