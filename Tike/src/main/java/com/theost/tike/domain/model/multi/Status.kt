package com.theost.tike.domain.model.multi

sealed class Status {

    object Loading : Status()
    object Success : Status()
    object Error : Status()
}