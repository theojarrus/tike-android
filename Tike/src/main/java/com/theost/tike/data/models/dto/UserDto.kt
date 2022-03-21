package com.theost.tike.data.models.dto

import com.google.firebase.firestore.DocumentId
import com.theost.tike.data.models.core.Lifestyle
import com.theost.tike.data.models.core.User

data class UserDto(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val nick: String = "",
    val email: String = "",
    val phone: String = "",
    val avatar: String = "",
    val lifestyles: List<String> = emptyList(),
    val friends: List<String> = emptyList(),
    val blocked: List<String> = emptyList()
)

fun UserDto.mapToUser(lifestyles: List<Lifestyle>): User {
    return User(
        id = id,
        name = name,
        nick = nick,
        email = email,
        phone = phone,
        avatar = avatar,
        lifestyles = lifestyles,
        friends = friends,
        blocked = blocked
    )
}