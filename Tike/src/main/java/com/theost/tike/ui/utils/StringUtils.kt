package com.theost.tike.ui.utils

import com.theost.tike.ui.extensions.isLettersOnly
import java.lang.StringBuilder

object StringUtils {

    fun isNameCorrect(name: String): Boolean {
        return name.split(" ").run { size == 2 && all { it.isLettersOnly() } }
    }

    fun isNickCorrect(nick: String): Boolean {
        return !nick.contains(Regex("[^=,\\da-zA-Z\\s]|(?<!,)\\s"))
    }

    fun formatNameLetterCase(name: String): String {
        return StringBuilder().apply {
            name.split(" ").forEach { namePart ->
                append(namePart.replaceFirstChar { it.uppercase() })
                append(" ")
            }
        }.toString().trim()
    }

    fun formatNickLetterCase(nick: String): String {
        return nick.lowercase().trim()
    }

    fun formatName(name: String): String {
        return formatNameLetterCase(name)
    }

    fun formatNick(nick: String): String {
        return formatNickLetterCase(nick)
            .replace(Regex("[^=,\\da-zA-Z\\s]|(?<!,)\\s"), "")
    }
}