package com.family.memoir.module.user.vo;

public record TokenVO(String accessToken, String refreshToken, long expiresInSeconds) {
}
