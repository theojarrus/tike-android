package com.theost.tike.domain.model.core.mapper

import com.google.firebase.auth.FirebaseUser
import com.theost.tike.common.util.ApiUtils.getQualityAvatar
import com.theost.tike.common.util.StringUtils.formatName
import com.theost.tike.common.util.StringUtils.formatNick
import com.theost.tike.domain.model.core.User

class UserFirebaseMapper : (FirebaseUser) -> User {

    override fun invoke(user: FirebaseUser): User = with(user) {
        User(
            uid = uid,
            name = formatName(user.displayName.orEmpty()),
            nick = formatNick(user.displayName.orEmpty()),
            email = email.orEmpty(),
            phone = phoneNumber.orEmpty(),
            avatar = getQualityAvatar(photoUrl.toString()),
            friends = emptyList(),
            requesting = emptyList(),
            pending = emptyList(),
            blocked = emptyList(),
            lifestyles = emptyList(),
            isActive = true
        )
    }
}
