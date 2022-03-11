package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.data.models.ui.ListButton
import com.theost.tike.databinding.ItemButtonBinding
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class ButtonAdapterDelegate(private val clickListener: () -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(enabled)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ListButton

    class ViewHolder(
        private val binding: ItemButtonBinding,
        private val clickListener: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(enabled: Boolean) {
            binding.root.setOnClickListener { clickListener() }
            binding.root.isEnabled = enabled
        }

    }

}