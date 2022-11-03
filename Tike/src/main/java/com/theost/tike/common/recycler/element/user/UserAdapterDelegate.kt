package com.theost.tike.common.recycler.element.user

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.R
import com.theost.tike.common.extension.load
import com.theost.tike.common.recycler.delegate.AdapterDelegate
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.databinding.ItemUserBinding

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
