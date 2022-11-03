package com.theost.tike.core.presentation

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.model.exception.StateException

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
        if (newState != oldState) update(newState)
    }

    private fun update(newState: State) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            _state.value = newState
        } else {
            _state.postValue(newState)
        }
        println(newState)
    }
}
