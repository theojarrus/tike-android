package com.theost.tike.domain.config

enum class Toggle(val key: String, val description: String, val defaultValue: Boolean) {

    APP_VERSION_SUPPORTED(
        key = "app_version_supported",
        description = "Current application version is supported",
        defaultValue = true
    )
}
