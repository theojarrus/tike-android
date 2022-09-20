package com.theost.tike.data.models.state

import com.theost.tike.data.models.core.Location

sealed class OptionAction {

    class LocationOptionAction(val location: Location) : OptionAction()
}