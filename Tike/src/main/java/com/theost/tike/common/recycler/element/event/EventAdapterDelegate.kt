package com.theost.tike.common.recycler.element.event

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.R
import com.theost.tike.common.extension.load
import com.theost.tike.common.recycler.delegate.AdapterDelegate
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.databinding.ItemAvatarBinding
import com.theost.tike.databinding.ItemEventBinding
import com.theost.tike.domain.model.multi.EventAction
import com.theost.tike.domain.model.multi.EventAction.*
import com.theost.tike.domain.model.multi.EventMode
import com.theost.tike.domain.model.multi.EventMode.*

class EventAdapterDelegate(private val clickListener: (EventAction) -> Unit) : AdapterDelegate {

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
        private val clickListener: (EventAction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventUi) {
            with(binding) {
                setupWithMode(item.mode)
                eventTitle.text = item.title
                eventDescription.text = item.description
                eventTime.text = item.time
                eventDate.text = item.date
                with(item.participants) {
                    eventParticipant1.displayAvatar(getOrNull(0))
                    eventParticipant2.displayAvatar(getOrNull(1))
                    eventParticipant3.displayAvatar(getOrNull(2))
                    eventParticipantCounter.let { counter ->
                        counter.root.isGone = size <= 3
                        counter.badgeCount.text = size.toString()
                    }
                }
                root.setOnClickListener {
                    clickListener(
                        Info(
                            item.id,
                            item.creator,
                            item.participants
                        )
                    )
                }
                acceptButton.setOnClickListener {
                    clickListener(
                        Accept(
                            item.id,
                            item.creator,
                            item.participants,
                            item.mode
                        )
                    )
                }
                rejectButton.setOnClickListener {
                    clickListener(
                        Reject(
                            item.id,
                            item.creator,
                            item.participants,
                            item.mode
                        )
                    )
                }
            }
        }

        private fun ItemEventBinding.setupWithMode(mode: EventMode) {
            when (mode) {
                SCHEDULE_PROPER, SCHEDULE_REFERENCE -> {
                    eventDate.isGone = true
                    acceptButton.isGone = true
                }
                REQUESTING_OUT, PENDING_OUT -> {
                    acceptButton.isGone = true
                }
                JOINING -> {
                    rejectButton.isGone = true
                }
                else -> {}
            }
        }

        private fun ItemAvatarBinding.displayAvatar(user: UserUi?) {
            root.isGone = user == null
            user?.let {
                when (it.hasAccess) {
                    true -> participantAvatar.load(it.avatar)
                    false -> participantAvatar.load(R.drawable.ic_blocked)
                }
            }
        }
    }
}
