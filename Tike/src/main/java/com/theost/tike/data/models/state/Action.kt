package com.theost.tike.data.models.state

sealed class Action {

    class Delete(val id: String) : Action()
    class Info(val id: String) : Action()
}
