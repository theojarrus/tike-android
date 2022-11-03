package com.theost.tike.common.recycler.element.friend

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.R
import com.theost.tike.common.extension.load
import com.theost.tike.common.recycler.delegate.AdapterDelegate
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.databinding.ItemFriendBinding
import com.theost.tike.domain.model.multi.Direction.Out
import com.theost.tike.domain.model.multi.FriendAction
import com.theost.tike.domain.model.multi.FriendAction.*

class FriendAdapterDelegate(
    private val actionListener: (FriendAction) -> Unit
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemFriendBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding, actionListener)
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
        private val actionListener: (FriendAction) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FriendUi) {
            with(binding) {
                root.setOnClickListener { actionListener(Info(item.uid)) }
                block.setOnClickListener { actionListener(Block(item.uid)) }
                accept.setOnClickListener { actionListener(Accept(item.uid)) }
                decline.setOnClickListener {
                    actionListener(Decline(item.uid, item.direction))
                }
                accept.isGone = item.direction is Out
                block.isGone = item.direction is Out
                name.text = item.name ?: itemView.context.getString(R.string.no_user)
                nick.text = item.nick ?: itemView.context.getString(R.string.no_nick)
                when {
                    item.avatar != null && item.hasAccess -> avatar.load(item.avatar)
                    item.avatar != null && !item.hasAccess -> avatar.load(R.drawable.ic_blocked)
                    else ->avatar.load(R.drawable.ic_deleted)
                }
            }
        }
    }
}
