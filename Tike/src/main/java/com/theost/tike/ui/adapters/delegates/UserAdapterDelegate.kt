package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.R
import com.theost.tike.data.models.ui.UserUi
import com.theost.tike.databinding.ItemUserBinding
import com.theost.tike.ui.extensions.loadWithFadeIn
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class UserAdapterDelegate(private val clickListener: (uid: String) -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemUserBinding.inflate(from(parent.context), parent, false)
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
        private val binding: ItemUserBinding,
        private val clickListener: (uid: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserUi) {
            with(binding) {
                root.setOnClickListener { clickListener(item.uid) }
                userName.text = item.name
                userNick.text = item.nick
                when (item.hasAccess) {
                    true -> userAvatar.loadWithFadeIn(item.avatar)
                    false -> userAvatar.loadWithFadeIn(R.drawable.ic_blocked)
                }
            }
        }
    }
}
