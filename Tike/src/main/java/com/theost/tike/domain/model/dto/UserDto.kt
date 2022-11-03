package com.theost.tike.domain.model.dto

import com.google.firebase.firestore.DocumentId
import com.theost.tike.domain.model.core.Lifestyle
import com.theost.tike.domain.model.core.User

data class UserDto(
    @DocumentId
    val uid: String = "",
    val name: String = "",
    val nick: String = "",
    val email: String = "",
    val phone: String = "",
    val avatar: String = "",
    val friends: List<String> = emptyList(),
    val requesting: List<String> = emptyList(),
    val pending: List<String> = emptyList(),
    val blocked: List<String> = emptyList(),
    val lifestyles: List<String> = emptyList(),
    val active: Boolean = true
)

fun UserDto.mapToUser(lifestyles: List<Lifestyle> = emptyList()): User {
    return User(
        uid = uid,
        name = name,
        nick = nick,
        email = email,
        phone = phone,
        avatar = avatar,
        friends = friends,
        requesting = requesting,
        pending = pending,
        blocked = blocked,
        lifestyles = lifestyles,
        isActive = active
    )
}
