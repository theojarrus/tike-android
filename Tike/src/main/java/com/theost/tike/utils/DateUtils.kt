package com.theost.tike.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatMonthYear(month: Int, year: Int) : String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.YEAR, year)
        return SimpleDateFormat("LLLL, yyyy", Locale.getDefault()).format(calendar.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

}