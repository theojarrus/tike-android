package com.theost.tike.ui.adapters.delegates

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.data.models.ui.PlusButtonUi
import com.theost.tike.databinding.ItemAddButtonBinding
import com.theost.tike.ui.interfaces.AdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem

class AddButtonAdapterDelegate(private val clickListener: () -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemAddButtonBinding.inflate(from(parent.context), parent, false)
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

    override fun isOfViewType(item: DelegateItem): Boolean = item is PlusButtonUi

    class ViewHolder(
        private val binding: ItemAddButtonBinding,
        private val clickListener: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(enabled: Boolean) {
            with(binding.root) {
                when (enabled) {
                    true -> setOnClickListener { clickListener() }
                    false -> {
                        isGone = true
                        setOnClickListener(null)
                    }
                }
            }
        }
    }
}