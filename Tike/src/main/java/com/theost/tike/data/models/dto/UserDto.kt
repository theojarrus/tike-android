package com.theost.tike.data.models.dto

import com.google.firebase.firestore.DocumentId
import com.theost.tike.data.models.core.Lifestyle
import com.theost.tike.data.models.core.User

data class UserDto(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val nick: String = "",
    val email: String = "",
    val phone: String = "",
    val avatar: String = "",
    val lifestyles: List<String> = emptyList(),
    val friends: List<String> = emptyList(),
    val pending: List<String> = emptyList(),
    val blocked: List<String> = emptyList()
)

fun UserDto.mapToUser(lifestyles: List<Lifestyle> = emptyList()): User {
    return User(
        uid = uid,
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
