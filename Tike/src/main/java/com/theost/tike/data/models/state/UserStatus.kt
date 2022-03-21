package com.theost.tike.data.models.state

sealed class UserStatus {
    object Exist : UserStatus()
    object NotFound : UserStatus()
}
