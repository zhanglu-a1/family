package com.family.memoir.infra.storage;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.family.memoir.common.exception.BizException;
import com.family.memoir.common.exception.ErrorCode;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Service
public class AliyunOssStorageService {

    private static final Logger log = LoggerFactory.getLogger(AliyunOssStorageService.class);
    private static final DateTimeFormatter DATE_PATH = DateTimeFormatter.ofPattern("yyyy/MM");

    private final StorageProperties properties;
    private final OSS ossClient;

    public AliyunOssStorageService(StorageProperties properties) {
        this.properties = properties;
        this.ossClient = createClientIfEnabled(properties);
    }

    public UploadResult upload(MultipartFile file, long spaceId) {
        ensureEnabled();
        validateFile(file);

        String extension = extensionOf(file.getOriginalFilename());
        String objectKey = buildObjectKey(spaceId, extension);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(contentTypeOf(file));

        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(properties.bucketName(), objectKey, inputStream, metadata);
        } catch (IOException | OSSException ex) {
            log.error("OSS 上传失败, key={}", objectKey, ex);
            throw new BizException(ErrorCode.STORAGE_UPLOAD_FAILED);
        }
        return new UploadResult(objectKey, signedUrl(objectKey));
    }

    public String signedUrl(String objectKey) {
        ensureEnabled();
        if (!StringUtils.hasText(objectKey) || objectKey.startsWith("/") || objectKey.contains("..")) {
            throw new BizException(ErrorCode.PARAM_ERROR, "非法的文件 key");
        }
        Date expiration = new Date(System.currentTimeMillis() + properties.signedUrlExpire().toMillis());
        return ossClient.generatePresignedUrl(properties.bucketName(), objectKey, expiration, HttpMethod.GET)
                .toString();
    }

    public long signedUrlExpiresInSeconds() {
        return properties.signedUrlExpire().toSeconds();
    }

    @PreDestroy
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    private void ensureEnabled() {
        if (ossClient == null) {
            throw new BizException(ErrorCode.STORAGE_NOT_CONFIGURED);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ErrorCode.FILE_EMPTY);
        }
        if (file.getSize() > properties.maxFileSize().toBytes()) {
            throw new BizException(ErrorCode.FILE_TOO_LARGE,
                    "文件大小不能超过 " + properties.maxFileSize());
        }
        String extension = extensionOf(file.getOriginalFilename());
        boolean allowed = properties.allowedExtensions() != null
                && properties.allowedExtensions().stream()
                .filter(StringUtils::hasText)
                .anyMatch(item -> item.trim().toLowerCase(Locale.ROOT).equals(extension));
        if (!allowed) {
            throw new BizException(ErrorCode.FILE_TYPE_NOT_ALLOWED, "不支持的文件类型: " + extension);
        }
    }

    private String buildObjectKey(long spaceId, String extension) {
        String prefix = properties.dirPrefix() == null ? "" : properties.dirPrefix();
        if (!prefix.isEmpty() && !prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix + spaceId + "/" + LocalDate.now().format(DATE_PATH) + "/"
                + UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    private String extensionOf(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private String contentTypeOf(MultipartFile file) {
        return StringUtils.hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream";
    }

    private OSS createClientIfEnabled(StorageProperties properties) {
        if (!properties.enabled()) {
            log.info("阿里云 OSS 未启用（family.oss.enabled=false），上传接口将返回配置提示");
            return null;
        }
        if (!StringUtils.hasText(properties.endpoint())
                || !StringUtils.hasText(properties.accessKeyId())
                || !StringUtils.hasText(properties.accessKeySecret())
                || !StringUtils.hasText(properties.bucketName())) {
            throw new IllegalStateException("OSS 已启用但配置不完整，请检查 OSS_ENDPOINT / OSS_ACCESS_KEY_ID / OSS_ACCESS_KEY_SECRET / OSS_BUCKET");
        }
        return new OSSClientBuilder().build(
                properties.endpoint(), properties.accessKeyId(), properties.accessKeySecret());
    }
}
