package com.theost.tike.domain.model.dto.mapper

import com.theost.tike.domain.model.core.Lifestyle
import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.dto.UserDto

class UserDtoMapper : (User) -> UserDto {

    override fun invoke(user: User): UserDto = with(user) {
        UserDto(
            uid = uid,
            name = name,
            nick = nick,
            email = email,
            phone = phone,
            avatar = avatar,
            friends = friends,
            requesting = requesting,
            pending = pending,
            blocked = blocked,
            lifestyles = lifestyles.map(Lifestyle::id),
            active = isActive
        )
    }
}
