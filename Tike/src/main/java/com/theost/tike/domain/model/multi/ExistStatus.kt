package com.theost.tike.domain.model.multi

sealed class ExistStatus {

    object Exist : ExistStatus()
    object NotFound : ExistStatus()
}
