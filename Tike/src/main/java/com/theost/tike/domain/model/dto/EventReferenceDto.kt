package com.theost.tike.domain.model.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class EventReferenceDto(
    @DocumentId
    val id: String = "",
    val type: String = "",
    val weekDay: Int = 0,
    val monthDay: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val repeatMode: String = "",
    val reference: DocumentReference = Firebase.firestore.document("")
)
