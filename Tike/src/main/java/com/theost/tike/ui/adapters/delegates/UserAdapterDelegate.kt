package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.theost.tike.R
import com.theost.tike.data.models.ui.UserUi
import com.theost.tike.databinding.ItemUserBinding
import com.theost.tike.ui.extensions.load
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class UserAdapterDelegate(
    private val clickListener: (userId: String, isSelected: Boolean) -> Unit
) : AdapterDelegate {

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
        private val clickListener: (userId: String, isSelected: Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserUi) {
            with(binding) {
                userAvatar.load(item.avatar, R.color.blue, R.color.blue)
                userName.text = item.name
                userNick.text = item.nick
                indicatorSelected.isVisible = (item.isSelected)
                root.setOnClickListener { clickListener(item.id, !item.isSelected) }
            }
        }
    }
}