package com.theost.tike.data.models.core

import com.theost.tike.data.models.ui.ParticipantUi
import com.theost.tike.data.models.ui.UserUi

data class User(
    val id: String,
    val name: String,
    val nick: String,
    val email: String,
    val phone: String,
    val avatar: String,
    val lifestyles: List<Lifestyle>,
    val friends: List<String>,
    val blocked: List<String>
)