package com.theost.tike.domain.widget

import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.androidhuman.rxfirebase2.auth.RxFirebaseUser
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Single

object RxFirebaseAuthUser {

    fun tryReload(instance: FirebaseAuth): Single<FirebaseUser> {
        return RxFirebaseAuth.getCurrentUser(instance)
            .flatMapSingle { firebaseUser ->
                RxFirebaseUser.reload(firebaseUser)
                    .andThen(Single.just(firebaseUser))
                    .onErrorResumeNext { error ->
                        when (error) {
                            is FirebaseNetworkException -> Single.just(firebaseUser)
                            else -> Single.error(error)
                        }
                    }
            }
    }

    fun requireReload(instance: FirebaseAuth): Single<FirebaseUser> {
        return RxFirebaseAuth.getCurrentUser(instance)
            .flatMapSingle { firebaseUser ->
                RxFirebaseUser.reload(firebaseUser)
                    .andThen(Single.just(firebaseUser))
            }
    }
}
