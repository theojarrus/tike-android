package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.R
import com.theost.tike.data.models.ui.ParticipantUi
import com.theost.tike.databinding.ItemParticipantBinding
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.extensions.loadWithFadeIn
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class ParticipantAdapterDelegate(private val clickListener: (uid: String) -> Unit) : AdapterDelegate {

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
        (holder as ViewHolder).bind(item as ParticipantUi)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ParticipantUi

    class ViewHolder(
        private val binding: ItemParticipantBinding,
        private val clickListener: (uid: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ParticipantUi) {
            with(binding) {
                root.setOnClickListener { clickListener(item.uid) }
                userName.text = item.name
                userNick.text = item.nick
                indicatorSelected.isVisible = (item.isSelected)
                when (item.hasAccess) {
                    true -> userAvatar.loadWithFadeIn(item.avatar)
                    false -> userAvatar.loadWithFadeIn(R.drawable.ic_blocked)
                }
            }
        }
    }
}
