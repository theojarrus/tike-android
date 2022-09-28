package com.theost.tike.common.util

import com.theost.tike.common.extension.isLettersOnly

object StringUtils {

    fun isNameCorrect(name: String): Boolean {
        return name.trim().split(" ").run { size == 2 && all { it.isLettersOnly() } }
    }

    fun isNickCorrect(nick: String): Boolean {
        return !nick.trim().contains(Regex("[^=,\\da-zA-Z\\s]|(?<!,)\\s"))
    }

    fun formatNameLetterCase(name: String): String {
        return name.split(" ").joinToString(" ") { part ->
            part.replaceFirstChar(Char::uppercase)
        }
    }

    fun formatNickLetterCase(nick: String): String {
        return nick.lowercase()
    }

    fun formatName(name: String): String {
        return formatNameLetterCase(name)
    }

    fun formatNick(nick: String): String {
        return formatNickLetterCase(nick)
            .replace(Regex("[^=,\\da-zA-Z\\s]|(?<!,)\\s"), "")
    }

    fun switchIfEmpty(value: String, replace: String): String {
        return value.trim().run { if (isEmpty()) replace else value }
    }
}
