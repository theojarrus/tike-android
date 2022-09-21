package com.theost.tike.core.recycler.title

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.common.recycler.delegate.AdapterDelegate
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.databinding.ItemTitleBinding

class TitleAdapterDelegate : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemTitleBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as TitleUi)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is TitleUi

    class ViewHolder(
        private val binding: ItemTitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TitleUi) {
            binding.title.text = itemView.context.getString(item.stringRes)
        }
    }
}
