package com.theost.tike.feature.info.mapper

import com.theost.tike.R
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.element.option.OptionUi
import com.theost.tike.common.recycler.element.title.TitleUi
import com.theost.tike.common.recycler.element.user.UserUi
import com.theost.tike.domain.model.core.Location
import com.theost.tike.domain.model.multi.OptionAction.LocationOptionAction

class InfoMapper : (
    String,
    UserUi,
    Location?,
    List<UserUi>,
    List<UserUi>, List<UserUi>
) -> List<DelegateItem> {

    override fun invoke(
        auid: String,
        creator: UserUi,
        location: Location?,
        members: List<UserUi>,
        pending: List<UserUi>,
        requesting: List<UserUi>
    ): List<DelegateItem> {
        return buildList {
            if (location != null) {
                add(TitleUi(R.string.info))
                add(OptionUi(
                    R.string.location,
                    location.address,
                    R.drawable.ic_location,
                    LocationOptionAction(location)
                ))
            }
            if (creator.uid != auid) {
                add(TitleUi(R.string.creator))
                add(creator)
            }
            members.filterUsers(auid, creator.uid)?.let { members ->
                add(TitleUi(R.string.event_participants))
                addAll(members)
            }
            pending.filterUsers(auid, creator.uid)?.let { pending ->
                add(TitleUi(R.string.event_pending))
                addAll(pending)
            }
            requesting.filterUsers(auid, creator.uid)?.let { requesting ->
                add(TitleUi(R.string.event_requesting))
                addAll(requesting)
            }
        }
    }

    private fun List<UserUi>.filterUsers(auid: String, creator: String): List<UserUi>? {
        return filter { it.uid != auid && it.uid != creator }.takeIf(List<UserUi>::isNotEmpty)
    }
}
