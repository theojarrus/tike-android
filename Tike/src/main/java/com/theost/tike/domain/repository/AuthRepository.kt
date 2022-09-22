package com.theost.tike.domain.repository

import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.androidhuman.rxfirebase2.auth.RxFirebaseUser
import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.common.extension.getOrNull
import com.theost.tike.common.extension.isTrue
import com.theost.tike.domain.api.FirestoreApi.provideUserDocument
import com.theost.tike.domain.model.multi.AuthStatus
import com.theost.tike.domain.model.multi.AuthStatus.*
import com.theost.tike.domain.model.multi.ExistStatus
import com.theost.tike.domain.model.multi.ExistStatus.Exist
import com.theost.tike.domain.model.multi.ExistStatus.NotFound
import com.theost.tike.domain.util.RxFirebaseAuthUser
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object AuthRepository {

    fun signIn(credential: AuthCredential): Single<FirebaseUser> {
        return RxFirebaseAuth.signInWithCredential(Firebase.auth, credential)
            .subscribeOn(Schedulers.io())
    }

    fun signOut(): Completable {
        return RxFirebaseAuth.signOut(Firebase.auth)
            .subscribeOn(Schedulers.io())
    }

    fun delete(credential: AuthCredential): Completable {
        return RxFirebaseAuth.getCurrentUser(Firebase.auth).flatMapCompletable { firebaseUser ->
            UsersRepository.deleteUser(firebaseUser.uid)
                .andThen(RxFirebaseUser.reauthenticate(firebaseUser, credential))
                .andThen(RxFirebaseUser.delete(firebaseUser))
        }
    }

    fun getCurrentUser(): Maybe<FirebaseUser> {
        return RxFirebaseAuth.getCurrentUser(Firebase.auth).subscribeOn(Schedulers.io())
    }

    fun getUserAuthStatus(): Single<AuthStatus> {
        return RxFirebaseAuthUser.tryReload(Firebase.auth)
            .flatMap { getUserExist(it.uid) }
            .map { if (it is Exist) SignedIn else SigningUp }
            .onErrorReturn { SignedOut }
            .subscribeOn(Schedulers.io())
    }

    private fun getUserExist(uid: String): Single<ExistStatus> {
        return RxFirebaseFirestore.data(provideUserDocument(uid))
            .map { if (it.getOrNull()?.exists().isTrue()) Exist else NotFound }
            .subscribeOn(Schedulers.io())
    }

    fun observeUserAuthStatus(): Observable<AuthStatus> {
        return observeFirebaseUserStatus()
            .flatMap { firebaseAuth ->
                firebaseAuth.currentUser?.uid?.let { uid ->
                    observeDatabaseUserStatus(uid).map { userExist ->
                        when (userExist) {
                            is Exist -> SignedIn
                            is NotFound -> SigningUp
                        }
                    }
                } ?: Observable.just(SignedOut)
            }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
    }

    private fun observeFirebaseUserStatus(): Observable<FirebaseAuth> {
        return RxFirebaseAuth.authStateChanges(Firebase.auth)
            .subscribeOn(Schedulers.io())
    }

    private fun observeDatabaseUserStatus(uid: String): Observable<ExistStatus> {
        return RxFirebaseFirestore.dataChanges(provideUserDocument(uid))
            .map { if (it.getOrNull()?.exists().isTrue()) Exist else NotFound }
            .subscribeOn(Schedulers.io())
    }
}
