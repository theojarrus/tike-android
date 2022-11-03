package com.theost.tike.feature.profile.ui.model

sealed class ProfileAction {

    object Accept : ProfileAction()
    object Add : ProfileAction()
    object Block : ProfileAction()
    object Decline : ProfileAction()
    object Delete : ProfileAction()
    object Edit : ProfileAction()
    object Friends : ProfileAction()
    object Message : ProfileAction()
    object Preferences : ProfileAction()
    object Qr : ProfileAction()
    object Share : ProfileAction()
    object Unblock : ProfileAction()
}
