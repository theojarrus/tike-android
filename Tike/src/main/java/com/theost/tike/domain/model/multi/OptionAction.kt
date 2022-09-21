package com.theost.tike.domain.model.multi

import com.theost.tike.domain.model.core.Location

sealed class OptionAction {

    class LocationOptionAction(val location: Location) : OptionAction()
}