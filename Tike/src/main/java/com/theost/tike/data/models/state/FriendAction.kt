package com.theost.tike.data.models.state

sealed class FriendAction {

    class Accept(val id: String) : FriendAction()
    class Reject(val id: String) : FriendAction()
    class Block(val id: String) : FriendAction()
    class Info(val id: String) : FriendAction()
}
