package com.theost.tike.ui.adapters.basic

import android.content.Context
import android.widget.ArrayAdapter
import com.theost.tike.data.models.core.Location

class LocationsAdapter(context: Context) : ArrayAdapter<String>(
    context,
    android.R.layout.simple_dropdown_item_1line,
    emptyList()
) {

    private val items: MutableList<Location> = mutableListOf()

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): String {
        return items[position].address
    }

    fun getLocationItem(position: Int): Location {
        return items[position]
    }

    fun setItems(items: List<Location>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.items.clear()
        notifyDataSetChanged()
    }

    fun notMatches(address: String): Boolean {
        return items.isNotEmpty() && items.none { it.address == address }
    }
}