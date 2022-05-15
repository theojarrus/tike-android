package com.theost.tike.data.models.state

sealed class Action {

    class Accept(val id: String) : Action()
    class Reject(val id: String) : Action()
    class Block(val id: String) : Action()
    class Info(val id: String) : Action()
}
