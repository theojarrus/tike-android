package com.theost.tike.common.recycler.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.theost.tike.common.recycler.delegate.AdapterDelegate
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.delegate.DelegateItemCallback

open class BaseAdapter : ListAdapter<DelegateItem, RecyclerView.ViewHolder>(DelegateItemCallback()) {

    private val delegates: MutableList<AdapterDelegate> = mutableListOf()
    private var isEnabled: Boolean = true

    private class EmptyViewHolder(context: Context) : RecyclerView.ViewHolder(View(context))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType >= 0 && viewType < delegates.size) {
            delegates[viewType].onCreateViewHolder(parent)
        } else {
            EmptyViewHolder(parent.context)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            delegates[getItemViewType(position)].onBindViewHolder(holder, getItem(position), position, isEnabled)
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw Exception("Can't find delegate for item at position $position: ${getItem(position)}")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return delegates.indexOfFirst { it.isOfViewType(currentList[position]) }
    }

    fun addDelegate(delegate: AdapterDelegate) {
        delegates.add(delegate)
    }

    fun setEnabled(isEnabled: Boolean) {
        if (this.isEnabled != isEnabled) {
            this.isEnabled = isEnabled
            notifyItemRangeChanged(0, itemCount)
        }
    }
}
