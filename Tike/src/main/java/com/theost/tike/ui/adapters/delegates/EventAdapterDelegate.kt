package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.data.models.state.ActionMode
import com.theost.tike.data.models.state.ActionMode.CANCEL
import com.theost.tike.data.models.ui.EventUi
import com.theost.tike.databinding.ItemEventBinding
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class EventAdapterDelegate(private val clickListener: (String, ActionMode) -> Unit) :
    AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemEventBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as EventUi)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is EventUi

    class ViewHolder(
        private val binding: ItemEventBinding,
        private val clickListener: (String, ActionMode) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventUi) {
            with(binding) {
                eventTitle.text = item.title
                eventDescription.text = item.description
                eventTime.text = item.time
                cancelButton.setOnClickListener { clickListener(item.id, CANCEL) }
                item.participants.let { participants ->
                    participants.getOrNull(0)?.avatar?.let {
                        eventParticipant1.participantAvatar.load(it)
                    }
                    participants.getOrNull(1)?.avatar?.let {
                        eventParticipant2.participantAvatar.load(it)
                    }
                    participants.getOrNull(2)?.avatar?.let {
                        eventParticipant3.participantAvatar.load(it)
                    }
                    eventParticipantCounter.let { counter ->
                        counter.root.isGone = participants.size <= 3
                        counter.participantCount.text = participants.size.toString()
                    }
                }
            }
        }
    }
}