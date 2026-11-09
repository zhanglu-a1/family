package com.family.memoir.infra.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "family.oss")
public record StorageProperties(
        boolean enabled,
        String endpoint,
        String accessKeyId,
        String accessKeySecret,
        String bucketName,
        String dirPrefix,
        Duration signedUrlExpire,
        DataSize maxFileSize,
        List<String> allowedExtensions
) {
}
