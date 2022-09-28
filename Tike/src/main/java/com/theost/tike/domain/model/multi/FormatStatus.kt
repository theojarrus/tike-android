package com.theost.tike.domain.model.multi

sealed class FormatStatus {

    object Initial : FormatStatus()
    object Success : FormatStatus()
    object Error : FormatStatus()
}
