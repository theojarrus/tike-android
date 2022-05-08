package com.theost.tike.data.models.state

sealed class ExistStatus {

    object Exist : ExistStatus()
    object NotFound : ExistStatus()
}
