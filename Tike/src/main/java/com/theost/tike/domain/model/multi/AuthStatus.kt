package com.theost.tike.domain.model.multi

import com.theost.tike.domain.model.multi.AuthStatus.*

sealed class AuthStatus(val name: String) {

    object SignedIn : AuthStatus("signed_in")
    object SigningUp : AuthStatus("signing_up")
    object SignedOut : AuthStatus("signed_out")
}

fun getAuthStatusByName(name: String?): AuthStatus {
    return when (name) {
        SignedIn.name -> SignedIn
        SigningUp.name -> SigningUp
        else -> SignedOut
    }
}