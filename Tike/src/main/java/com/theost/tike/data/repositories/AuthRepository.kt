package com.theost.tike.data.repositories

import com.androidhuman.rxfirebase2.auth.RxFirebaseAuth
import com.androidhuman.rxfirebase2.auth.RxFirebaseUser
import com.androidhuman.rxfirebase2.firestore.RxFirebaseFirestore
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.api.FirestoreApi.provideUserDocument
import com.theost.tike.data.models.state.AuthStatus
import com.theost.tike.data.models.state.AuthStatus.*
import com.theost.tike.data.models.state.ExistStatus
import com.theost.tike.data.models.state.ExistStatus.Exist
import com.theost.tike.data.models.state.ExistStatus.NotFound
import com.theost.tike.ui.extensions.getOrNull
import com.theost.tike.ui.extensions.isTrue
import io.reactivex.Completable
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
        return RxFirebaseAuth.getCurrentUser(Firebase.auth).toSingle().flatMapCompletable { user ->
            UsersRepository.deleteUser(user.uid)
                .andThen(RxFirebaseUser.reauthenticate(user, credential))
                .andThen(RxFirebaseUser.delete(user))
        }
    }

    fun getUserAuthStatus(): Single<AuthStatus> {
        return RxFirebaseAuth.getCurrentUser(Firebase.auth)
            .flatMapCompletable { RxFirebaseUser.reload(it) }
            .andThen(RxFirebaseAuth.getCurrentUser(Firebase.auth))
            .flatMapSingle { getUserExist(it.uid) }
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
            .map { snapshot -> if (snapshot.getOrNull()?.exists().isTrue()) Exist else NotFound }
            .subscribeOn(Schedulers.io())
    }
}