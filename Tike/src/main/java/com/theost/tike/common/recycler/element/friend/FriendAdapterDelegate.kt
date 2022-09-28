package com.theost.tike.common.recycler.element.friend

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.common.extension.load
import com.theost.tike.common.recycler.delegate.AdapterDelegate
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.databinding.ItemFriendBinding
import com.theost.tike.domain.model.multi.FriendAction
import com.theost.tike.domain.model.multi.FriendAction.*
import com.theost.tike.domain.model.multi.FriendMode.PENDING

class FriendAdapterDelegate(private val clickListener: (FriendAction) -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemFriendBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as FriendUi)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is FriendUi

    class ViewHolder(
        private val binding: ItemFriendBinding,
        private val clickListener: (FriendAction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FriendUi) {
            with(binding) {
                userName.text = item.name
                userNick.text = item.nick
                userAvatar.load(item.avatar)
                acceptButton.isGone = item.mode != PENDING
                blockButton.isGone = item.mode != PENDING
                acceptButton.setOnClickListener { clickListener(Accept(item.uid)) }
                rejectButton.setOnClickListener { clickListener(Reject(item.uid)) }
                blockButton.setOnClickListener { clickListener(Block(item.uid)) }
                root.setOnClickListener { clickListener(Info(item.uid)) }
            }
        }
    }
}
