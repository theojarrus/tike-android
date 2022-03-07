package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.data.models.ui.ListParticipant
import com.theost.tike.databinding.ItemParticipantBinding
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class ParticipantAdapterDelegate(private val clickListener: (participantId: Int) -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int
    ) {
        (holder as ViewHolder).bind(item as ListParticipant)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ListParticipant

    class ViewHolder(
        private val binding: ItemParticipantBinding,
        private val clickListener: (participantId: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listParticipant: ListParticipant) {
            binding.participantName.text = listParticipant.name
            binding.removeParticipantButton.setOnClickListener { clickListener(listParticipant.id) }
        }

    }

}