package com.theost.tike.common.recycler.element.option

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.common.recycler.delegate.AdapterDelegate
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.databinding.ItemOptionBinding
import com.theost.tike.domain.model.multi.OptionAction

class OptionAdapterDelegate(
    private val clickListener: ((action: OptionAction) -> Unit)? = null
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemOptionBinding.inflate(from(parent.context), parent, false)
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int,
        enabled: Boolean
    ) {
        (holder as ViewHolder).bind(item as OptionUi)
    }

    override fun isOfViewType(item: DelegateItem): Boolean = item is OptionUi

    class ViewHolder(
        private val binding: ItemOptionBinding,
        private val clickListener: ((action: OptionAction) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OptionUi) {
            with(binding) {
                clickListener?.let { listener ->
                    item.action?.let { action ->
                        root.setOnClickListener {
                            listener(action)
                        }
                    }
                }
                arrow.isVisible = clickListener != null && item.action != null
                title.text = itemView.context.getString(item.title)
                subtitle.text = item.content
                avatar.setImageResource(item.icon)
            }
        }
    }
}
