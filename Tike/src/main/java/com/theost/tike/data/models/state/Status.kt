package com.theost.tike.data.models.state

sealed class Status {
    object Loading : Status()
    object Success : Status()
    class Error(error: Throwable) : Status()
}