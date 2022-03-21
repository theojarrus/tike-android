package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.theost.tike.R
import com.theost.tike.data.models.ui.ListUser
import com.theost.tike.databinding.ItemUserBinding
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class UserAdapterDelegate(
    private val clickListener: (userId: String, isSelected: Boolean) -> Unit
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as ListUser)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ListUser

    class ViewHolder(
        private val binding: ItemUserBinding,
        private val clickListener: (userId: String, isSelected: Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listUser: ListUser) {
            binding.userName.text = listUser.name
            binding.userNick.text = listUser.nick

            Glide.with(binding.root)
                .load(listUser.avatar)
                .placeholder(R.color.blue)
                .error(R.color.blue)
                .into(binding.userAvatar)

            binding.indicatorSelected.isVisible = (listUser.isSelected)
            binding.root.setOnClickListener { clickListener(listUser.id, !listUser.isSelected) }
        }

    }

}