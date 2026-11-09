package com.family.memoir.infra.storage;

public record SignedUrlVO(String url, long expiresInSeconds) {
}
