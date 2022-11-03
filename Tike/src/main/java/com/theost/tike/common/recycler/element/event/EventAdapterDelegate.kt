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
import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.EventAction
import com.theost.tike.domain.model.multi.EventAction.*
import com.theost.tike.domain.model.multi.EventType
import com.theost.tike.domain.model.multi.EventType.*

class EventAdapterDelegate(
    private val actionListener: (EventAction) -> Unit
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemEventBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding, actionListener)
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
        private val actionListener: (EventAction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventUi) {
            with(binding) {
                setupWithType(item.type)
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
                    actionListener(
                        Info(
                            item.id,
                            item.id(),
                            item.creator
                        )
                    )
                }
                accept.setOnClickListener {
                    actionListener(
                        Accept(
                            item.id,
                            item.id(),
                            item.creator,
                            item.participants.takeIf(List<UserUi>::isNotEmpty)?.first()?.uid.orEmpty(),
                            item.type
                        )
                    )
                }
                decline.setOnClickListener {
                    actionListener(
                        Decline(
                            item.id,
                            item.id(),
                            item.creator,
                            item.participants.takeIf(List<UserUi>::isNotEmpty)?.first()?.uid,
                            item.type
                        )
                    )
                }
            }
        }

        private fun ItemEventBinding.setupWithType(type: EventType) {
            when {
                type is Schedule -> {
                    eventDate.isGone = true
                    accept.isGone = true
                }
                type is Pending && type.direction is Out || type is Requesting && type.direction is Out -> {
                    accept.isGone = true
                }
                type is Joining -> {
                    decline.isGone = true
                }
            }
        }

        private fun ItemAvatarBinding.displayAvatar(user: UserUi?) {
            root.isGone = user == null
            when {
                user?.avatar != null && user.hasAccess -> avatar.load(user.avatar)
                user?.avatar != null && !user.hasAccess -> avatar.load(R.drawable.ic_blocked)
                else -> avatar.load(R.drawable.ic_deleted)
            }
        }
    }
}
