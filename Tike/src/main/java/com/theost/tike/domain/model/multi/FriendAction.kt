package com.theost.tike.domain.model.multi

sealed class FriendAction(val uid: String) {

    class Accept(uid: String) : FriendAction(uid)
    class Add(uid: String) : FriendAction(uid)
    class Block(uid: String) : FriendAction(uid)
    class Decline(uid: String, val direction: Direction) : FriendAction(uid)
    class Delete(uid: String) : FriendAction(uid)
    class Info(uid: String) : FriendAction(uid)
    class Unblock(uid: String) : FriendAction(uid)
}
