package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.theost.tike.R
import com.theost.tike.data.models.ui.ListParticipant
import com.theost.tike.databinding.ItemAvatarBinding
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class AvatarAdapterDelegate() : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as ListParticipant)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ListParticipant

    class ViewHolder(
        private val binding: ItemAvatarBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listParticipant: ListParticipant) {
            Glide.with(binding.root)
                .load(listParticipant.avatar)
                .placeholder(R.color.blue)
                .error(R.color.blue)
                .into(binding.participantAvatar)
        }

    }

}