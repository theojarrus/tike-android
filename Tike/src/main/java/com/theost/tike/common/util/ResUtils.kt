package com.theost.tike.common.util

import android.content.Context
import android.content.res.Resources.Theme
import android.util.TypedValue
import com.theost.tike.R
import java.util.*

object ResUtils {

    fun getAttrColor(context: Context, attrColorRes: Int): Int {
        val typedValue = TypedValue()
        val theme: Theme = context.theme
        theme.resolveAttribute(attrColorRes, typedValue, true)
        return typedValue.data
    }

    fun getWeekDays(context: Context, locale: Locale): List<String>  {
        val id = if (locale == Locale("ru")) R.array.week_days_ru else R.array.week_days_en
        return context.resources.getStringArray(id).toList()
    }
}