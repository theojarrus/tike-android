package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.R
import com.theost.tike.data.models.state.Action
import com.theost.tike.data.models.state.Action.*
import com.theost.tike.data.models.ui.EventUi
import com.theost.tike.data.models.ui.UserUi
import com.theost.tike.databinding.ItemAvatarBinding
import com.theost.tike.databinding.ItemEventBinding
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class EventAdapterDelegate(private val clickListener: (Action) -> Unit) : AdapterDelegate {

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
        private val clickListener: (Action) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventUi) {
            with(binding) {
                eventTitle.text = item.title
                eventDescription.text = item.description
                eventTime.text = item.time
                acceptButton.setOnClickListener { clickListener(Accept(item.id)) }
                rejectButton.setOnClickListener { clickListener(Reject(item.id)) }
                root.setOnLongClickListener { clickListener(Info(item.id)).run { true } }
                with (item.participants) {
                    eventParticipant1.displayAvatar(getOrNull(0))
                    eventParticipant2.displayAvatar(getOrNull(1))
                    eventParticipant3.displayAvatar(getOrNull(2))
                    eventParticipantCounter.let { counter ->
                        counter.root.isGone = size <= 3
                        counter.badgeCount.text = size.toString()
                    }
                }
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
