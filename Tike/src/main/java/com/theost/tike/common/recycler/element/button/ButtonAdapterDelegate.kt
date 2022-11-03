package com.theost.tike.common.recycler.element.button

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.button.MaterialButton
import com.theost.tike.R
import com.theost.tike.common.extension.inflate
import com.theost.tike.common.extension.setOnClickListener
import com.theost.tike.common.recycler.delegate.AdapterDelegate
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.databinding.ItemButtonBinding
import com.theost.tike.domain.model.multi.ButtonStyle
import com.theost.tike.domain.model.multi.ButtonStyle.Filled

class ButtonAdapterDelegate<T>(private val clickListener: (button: T) -> Unit) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_button), clickListener)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder<T>).bind(item as ButtonUi<T>, enabled)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is ButtonUi<*>

    class ViewHolder<T>(
        itemView: View,
        private val clickListener: (button: T) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        val binding: ItemButtonBinding by viewBinding()

        fun bind(item: ButtonUi<T>, enabled: Boolean) {
            binding.root.children.forEach { it.isGone = true }
            with(getButton(item.text, item.style)) {
                setOnClickListener(enabled) { clickListener(item.action) }
                item.text?.let { setText(it) }
                item.icon?.let { setIconResource(it) }
                isGone = false
            }
        }

        private fun getButton(text: Int?, style: ButtonStyle): MaterialButton = with(binding) {
            when {
                text != null -> texted
                style is Filled -> filled
                else -> outlined
            }
        }
    }
}
