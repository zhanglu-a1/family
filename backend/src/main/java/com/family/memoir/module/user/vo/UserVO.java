package com.family.memoir.module.user.vo;

import com.family.memoir.module.user.entity.SysUser;

public record UserVO(Long id, String username, String nickname, String avatarUrl) {

    public static UserVO from(SysUser user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname(), user.getAvatarUrl());
    }
}
