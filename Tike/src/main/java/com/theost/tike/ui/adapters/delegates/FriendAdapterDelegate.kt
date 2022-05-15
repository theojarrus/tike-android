package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.data.models.state.Action
import com.theost.tike.data.models.state.Action.*
import com.theost.tike.data.models.ui.UserUi
import com.theost.tike.databinding.ItemFriendBinding
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class FriendAdapterDelegate(private val clickListener: (Action) -> Unit) : AdapterDelegate {

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
        (holder as ViewHolder).bind(item as UserUi)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is UserUi

    class ViewHolder(
        private val binding: ItemFriendBinding,
        private val clickListener: (Action) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserUi) {
            with(binding) {
                userName.text = item.name
                userNick.text = item.nick
                userAvatar.load(item.avatar)
                acceptButton.setOnClickListener { clickListener(Accept(item.uid)) }
                rejectButton.setOnClickListener { clickListener(Reject(item.uid)) }
                blockButton.setOnClickListener { clickListener(Block(item.uid)) }
                root.setOnLongClickListener { clickListener(Info(item.uid)).run { true } }
            }
        }
    }
}
