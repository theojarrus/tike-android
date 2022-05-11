package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.R
import com.theost.tike.data.models.ui.ParticipantUi
import com.theost.tike.databinding.ItemParticipantBinding
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class ParticipantAdapterDelegate(private val clickListener: (participantId: String) -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemParticipantBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as ParticipantUi, enabled)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ParticipantUi

    class ViewHolder(
        private val binding: ItemParticipantBinding,
        private val clickListener: (participantId: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ParticipantUi, enabled: Boolean) {
            with(binding) {
                participantAvatar.load(item.avatar, R.color.blue, R.color.blue)
                participantName.text = item.name
                participantName.isEnabled = enabled
                removeParticipantButton.isEnabled = enabled
                when (enabled) {
                    true -> removeParticipantButton.setOnClickListener { clickListener(item.uid) }
                    else -> removeParticipantButton.setOnClickListener(null)
                }
            }
        }
    }
}