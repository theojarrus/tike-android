package com.theost.tike.common.util

import android.util.Log

object LogUtils {

    const val LOG_VIEW_MODEL_SPLASH = "view_model_splash"
    const val LOG_VIEW_MODEL_AUTH = "view_model_auth"
    const val LOG_VIEW_MODEL_SIGN_IN = "view_model_sign_in"
    const val LOG_VIEW_MODEL_SIGN_UP = "view_model_sign_up"
    const val LOG_VIEW_MODEL_DAY = "view_model_day"
    const val LOG_VIEW_MODEL_PARTICIPANTS = "view_model_participants"
    const val LOG_VIEW_MODEL_FRIENDS = "view_model_friends"
    const val LOG_VIEW_MODEL_CREATION = "view_model_creation"
    const val LOG_VIEW_MODEL_PROFILE = "view_model_profile"
    const val LOG_VIEW_MODEL_PREFERENCES = "view_model_preferences"
    const val LOG_VIEW_MODEL_INBOX = "view_model_inbox"
    const val LOG_VIEW_MODEL_JOINING = "view_model_joining"

    const val LOG_FRAGMENT_LOCATION = "fragment_location"

    inline fun <reified T : Any> logError(root: T, error: Throwable) {
        Log.e(root::class.simpleName, error.message.orEmpty())
        error.printStackTrace()
    }
}
