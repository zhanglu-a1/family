package com.family.memoir.module.user.vo;

public record LoginVO(String accessToken, String refreshToken, long expiresInSeconds, UserVO user) {
}
