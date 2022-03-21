package com.theost.tike.data.models.state

sealed class AuthStatus {
    object SignedIn : AuthStatus()
    object SignedUp : AuthStatus()
    object SignedOut : AuthStatus()
}
