package com.theost.tike.common.recycler.delegate

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class DelegateItemCallback : DiffUtil.ItemCallback<DelegateItem>() {

    override fun areItemsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean {
        return oldItem::class == newItem::class && oldItem.id() == newItem.id()
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean {
        return oldItem.content() == newItem.content()
    }
}