package com.theost.tike.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.AuthStatus
import com.theost.tike.data.models.state.UserStatus
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object AuthRepository {

    fun getAuthStatus(): Observable<AuthStatus> {
        return Observable.create<Result<FirebaseUser>> { emitter ->
            val authListener = FirebaseAuth.AuthStateListener { auth ->
                val user = auth.currentUser
                if (user != null) {
                    emitter.onNext(Result.success(user))
                } else {
                    emitter.onNext(Result.failure(Throwable("Firebase: User singed out")))
                }
            }
            val firebaseAuth = Firebase.auth.apply { addAuthStateListener(authListener) }
            emitter.setCancellable { firebaseAuth.removeAuthStateListener(authListener) }
        }.switchMap { result ->
            result.fold({ firebaseUser ->
                UsersRepository.getUserExist(firebaseUser.uid)
                    .map { userStatus ->
                        if (userStatus == UserStatus.Exist) {
                            Result.success(AuthStatus.SignedIn)
                        } else {
                            Result.success(AuthStatus.SignedUp)
                        }
                    }
                    .onErrorReturn { Result.failure(it) }
            }, { Observable.just(Result.failure(it)) })
        }.map { result ->
            result.fold({ authStatus -> authStatus }, { AuthStatus.SignedOut })
        }.switchMapSingle { status ->
            Single.create<AuthStatus> { emitter ->
                val user = Firebase.auth.currentUser
                if (user != null) {
                    user.reload().addOnCompleteListener { task ->
                        if (task.isSuccessful || task.exception !is FirebaseAuthInvalidUserException) {
                            emitter.onSuccess(status)
                        } else {
                            emitter.onSuccess(AuthStatus.SignedOut)
                        }
                    }
                } else {
                    emitter.onSuccess(AuthStatus.SignedOut)
                }
            }
        }.subscribeOn(Schedulers.io())
    }

}