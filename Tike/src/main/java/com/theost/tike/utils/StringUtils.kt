package com.theost.tike.utils

import java.lang.StringBuilder

object StringUtils {

    fun isNameCorrect(name: String): Boolean = name.trim().split(" ").size == 2

    fun formatName(name: String): String {
        val nameBuilder = StringBuilder()
        val names = name.trim().split(" ").map { it.replaceFirstChar { char -> char.uppercase() } }
        names.forEach { nameBuilder.append(it).append(" ") }
        return nameBuilder.toString().trim()
    }

    fun isNickCorrect(nick: String): Boolean = !nick.contains(Regex("[^=,\\da-zA-Z\\s]|(?<!,)\\s"))

    fun formatNick(nick: String): String {
        return nick.replace(Regex("[^=,\\da-zA-Z\\s]|(?<!,)\\s"), "")
            .replace(" ", "").lowercase()
    }

}