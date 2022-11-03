package com.theost.tike.core.model

sealed class StateStatus {

    object Error : StateStatus()
    object Loading : StateStatus()
    object Refreshing : StateStatus()
    object Reloading : StateStatus()
    object Success : StateStatus()
    object Initial : StateStatus()

    fun isLoaded(): Boolean {
        return this is Success || this is Refreshing
    }

    fun getLoadingStatus(): StateStatus {
        return when (this) {
            is Initial -> Loading
            is Error -> Reloading
            else -> Refreshing
        }
    }
}
