package com.theost.tike.domain.model.multi

sealed class EventRole {

    object Author : EventRole()
    object Member : EventRole()
}
