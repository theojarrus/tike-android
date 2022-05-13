package com.theost.tike.ui.widgets

import com.androidhuman.rxfirebase2.auth.RxFirebaseUser
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Single

object RxFirebaseAuthUser {

    /** Returns error only if exception cause is not network connection **/
    fun tryReload(firebaseUser: FirebaseUser): Single<FirebaseUser> {
        return RxFirebaseUser.reload(firebaseUser)
            .andThen(Single.just(firebaseUser))
            .onErrorResumeNext { error ->
                when (error) {
                    is FirebaseNetworkException -> Single.just(firebaseUser)
                    else -> Single.error(error)
                }
            }
    }
}
