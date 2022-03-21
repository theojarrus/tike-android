package com.theost.tike.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.theost.tike.data.models.state.Status

class AuthViewModel : ViewModel() {

    private val _loadingStatus = MutableLiveData<Status>()
    val loadingStatus: LiveData<Status> = _loadingStatus

    fun onViewLoaded() {
        _loadingStatus.postValue(Status.Success)
    }

    fun signIn(token: String) {
        _loadingStatus.postValue(Status.Loading)
        val credential = GoogleAuthProvider.getCredential(token, null)
        Firebase.auth.signInWithCredential(credential).addOnFailureListener {
            _loadingStatus.postValue(Status.Error)
        }
    }

}