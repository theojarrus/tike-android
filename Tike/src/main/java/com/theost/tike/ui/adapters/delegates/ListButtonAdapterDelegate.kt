package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.data.models.ui.ListButtonUi
import com.theost.tike.databinding.ItemListButtonBinding
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class ListButtonAdapterDelegate(private val clickListener: () -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemListButtonBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as ListButtonUi, enabled)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ListButtonUi

    class ViewHolder(
        private val binding: ItemListButtonBinding,
        private val clickListener: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListButtonUi, enabled: Boolean) {
            with(binding) {
                content.text = item.content
                root.isEnabled = enabled
                root.setOnClickListener { clickListener() }
            }
        }
    }
}