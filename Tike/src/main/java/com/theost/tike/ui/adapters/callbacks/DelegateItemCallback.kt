package com.theost.tike.ui.adapters.callbacks

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.theost.tike.ui.interfaces.DelegateItem

class DelegateItemCallback : DiffUtil.ItemCallback<DelegateItem>() {
    override fun areItemsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem::class == newItem::class && oldItem.id() == newItem.id()

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem.content() == newItem.content()
}