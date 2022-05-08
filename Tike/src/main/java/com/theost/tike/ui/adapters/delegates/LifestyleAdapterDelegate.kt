package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.data.models.ui.LifestyleUi
import com.theost.tike.databinding.ItemLifestyleBinding
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class LifestyleAdapterDelegate(private val clickListener: (id: String) -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemLifestyleBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as LifestyleUi, enabled)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is LifestyleUi

    class ViewHolder(
        private val binding: ItemLifestyleBinding,
        private val clickListener: (id: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LifestyleUi, enabled: Boolean) {
            with(binding) {
                content.text = item.text
                root.isSelected = item.isSelected
                when (enabled) {
                    true -> root.setOnClickListener { clickListener(item.id) }
                    false -> root.setOnClickListener(null)
                }
            }
        }
    }
}