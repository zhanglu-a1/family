package com.family.memoir.infra.storage;

import com.family.memoir.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件存储")
@RestController
@RequestMapping("/api/v1/storage")
public class StorageController {

    private final AliyunOssStorageService storageService;

    public StorageController(AliyunOssStorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(summary = "服务端中转上传文件到阿里云 OSS")
    @PostMapping("/upload")
    public Result<UploadResult> upload(@RequestParam("file") MultipartFile file,
                                       @RequestParam(defaultValue = "0") long spaceId) {
        return Result.ok(storageService.upload(file, spaceId));
    }

    @Operation(summary = "获取私有文件的短期签名地址")
    @GetMapping("/signed-url")
    public Result<SignedUrlVO> signedUrl(@RequestParam String key) {
        return Result.ok(new SignedUrlVO(storageService.signedUrl(key), storageService.signedUrlExpiresInSeconds()));
    }
}
