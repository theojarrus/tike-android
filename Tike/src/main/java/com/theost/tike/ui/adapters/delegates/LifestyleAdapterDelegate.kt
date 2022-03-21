package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.data.models.ui.ListLifestyle
import com.theost.tike.databinding.ItemLifestyleBinding
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class LifestyleAdapterDelegate(private val clickListener: (id: String) -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemLifestyleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as ListLifestyle)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ListLifestyle

    class ViewHolder(
        private val binding: ItemLifestyleBinding,
        private val clickListener: (id: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listLifestyle: ListLifestyle) {
            binding.text.text = listLifestyle.text
            binding.text.setOnClickListener { clickListener(listLifestyle.id) }
            binding.root.isSelected = listLifestyle.isSelected
        }

    }

}