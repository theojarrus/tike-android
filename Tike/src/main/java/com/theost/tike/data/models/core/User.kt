package com.theost.tike.data.models.core

data class User(
    val uid: String,
    val name: String,
    val nick: String,
    val email: String,
    val phone: String,
    val avatar: String,
    val friends: List<String>,
    val requesting: List<String>,
    val pending: List<String>,
    val blocked: List<String>,
    val lifestyles: List<Lifestyle>
)
