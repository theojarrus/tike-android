package com.theost.tike.ui.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatMonthYear(month: Int, year: Int): String {
        return SimpleDateFormat("LLLL, yyyy", Locale.getDefault())
            .format(
                Calendar.getInstance().apply {
                    set(Calendar.MONTH, month - 1)
                    set(Calendar.YEAR, year)
                }.time
            )
            .replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
            }
    }

    fun formatDate(year: Int, month: Int, day: Int): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
            Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
            }.time
        )
    }

    fun formatTime(hour: Int, minute: Int): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }.time
        )
    }

    fun formatBeginEndTime(beginHour: Int, beginMinute: Int, endHour: Int, endMinute: Int): String {
        return "${formatTime(beginHour, beginMinute)} - ${formatTime(endHour, endMinute)}"
    }
}