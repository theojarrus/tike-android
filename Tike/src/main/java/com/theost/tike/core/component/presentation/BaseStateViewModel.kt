package com.theost.tike.core.component.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.tike.core.component.exception.StateException

abstract class BaseStateViewModel<State : BaseState> : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

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
    }
}
