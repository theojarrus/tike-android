package com.theost.tike.core.component.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.theost.tike.core.component.exception.StateException
import com.theost.tike.core.component.model.StateStatus

abstract class BaseStateViewModel<State : BaseState> : BaseRxViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    val status: StateStatus
        get() = state.value?.status ?: throw StateException()

    fun init(initialState: State, initialAction: () -> Unit) {
        if (_state.value == null) {
            _state.value = initialState
            initialAction()
        }
    }

    fun update(block: State.() -> State) {
        val oldState = state.value ?: throw StateException()
        val newState = block(oldState)
        if (newState != oldState) _state.postValue(newState)
        Log.wtf("NEWSTATE", "${newState.status}, $newState")
    }
}
