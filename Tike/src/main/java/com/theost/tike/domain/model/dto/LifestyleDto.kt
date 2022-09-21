package com.theost.tike.domain.model.dto

import com.google.firebase.firestore.DocumentId
import com.theost.tike.domain.model.core.Lifestyle

data class LifestyleDto(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val text: String = "",
    val extra: List<String> = emptyList()
)

fun LifestyleDto.mapToLifestyle(): Lifestyle {
    return Lifestyle(
        id = id,
        name = name,
        icon = icon,
        text = text,
        extra = extra
    )
}