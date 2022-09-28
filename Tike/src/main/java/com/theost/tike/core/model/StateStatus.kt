package com.theost.tike.core.model

sealed class StateStatus {

    object Error : StateStatus()
    object Loading : StateStatus()
    object Refreshing : StateStatus()
    object Success : StateStatus()
    object Initial : StateStatus()

    fun isLoaded(): Boolean {
        return this is Success || this is Refreshing
    }

    fun getLoadingStatus(): StateStatus {
        return if (this is Initial) Loading else Refreshing
    }
}
