package com.theost.tike.data.models.core

data class Lifestyle(
    val id: String,
    val name: String,
    val icon: String,
    val text: String,
    val extra: List<String>
)