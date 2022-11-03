package com.theost.tike.feature.profile.ui.widget

import androidx.appcompat.widget.Toolbar
import com.theost.tike.R
import com.theost.tike.common.extension.setSingleOnItemClickListener
import com.theost.tike.core.model.StateStatus
import com.theost.tike.core.model.StateStatus.Loading
import com.theost.tike.feature.profile.ui.model.ProfileAction
import com.theost.tike.feature.profile.ui.model.ProfileAction.Block
import com.theost.tike.feature.profile.ui.model.ProfileAction.Unblock

class ToolbarMenuAdapter(
    private val toolbar: Toolbar,
    private val clickListener: (ProfileAction) -> Unit
) {

    private val blockItem = toolbar.menu.findItem(R.id.profileBlock)
    private val unblockItem = toolbar.menu.findItem(R.id.profileUnblock)

    init {
        toolbar.setSingleOnItemClickListener { item ->
            when (item.itemId) {
                R.id.profileBlock -> clickListener(Block)
                R.id.profileUnblock -> clickListener(Unblock)
            }
        }
    }

    fun update(status: StateStatus?, isActive: Boolean?, isActual: Boolean?, isBlocked: Boolean?) {
        if (isActive != null && isActual != null && isBlocked != null) {
            blockItem.isEnabled = status !is Loading && !isActual
            unblockItem.isEnabled = status !is Loading && !isActual
            blockItem.isVisible = isActive && !isBlocked
            unblockItem.isVisible = isActive && isBlocked
            if (!isActual && toolbar.navigationIcon == null) {
                toolbar.setNavigationIcon(R.drawable.ic_back)
            }
        } else {
            blockItem.isVisible = false
            unblockItem.isVisible = false
        }
    }
}
