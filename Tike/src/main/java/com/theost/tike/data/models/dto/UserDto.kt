package com.theost.tike.data.models.dto

import com.google.firebase.firestore.DocumentId
import com.theost.tike.data.models.core.User

data class UserDto(
    @DocumentId
    val id: String = "",
    val firstName: String = "",
    val secondName: String = "",
    val nickName: String = "",
    val email: String = "",
    val phone: String = "",
    val avatar: String = "",
    val friends: List<String> = emptyList(),
    val blocked: List<String> = emptyList()
)

fun UserDto.mapToUser(): User {
    return User(
        id = id,
        firstName = firstName,
        secondName = secondName,
        nickName = nickName,
        email = email,
        phone = phone,
        avatar = avatar,
        friends = friends,
        blocked = blocked
    )
}