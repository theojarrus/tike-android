package com.theost.tike.domain.model.multi

import java.io.Serializable

sealed class AuthStatus: Serializable {

    object SignedIn : AuthStatus()
    object SignedOut : AuthStatus()
    object SigningUp : AuthStatus()
    object Unknown : AuthStatus()
}
