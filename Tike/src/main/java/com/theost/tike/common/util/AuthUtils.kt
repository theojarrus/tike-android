package com.theost.tike.common.util

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.theost.tike.domain.api.FirestoreApi.SERVER_CLIENT_ID

object AuthUtils {

    fun getSignInIntent(activity: Activity): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SERVER_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso).signInIntent
    }
}