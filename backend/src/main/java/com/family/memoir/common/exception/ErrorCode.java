package com.family.memoir.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    OK(0, "ok", HttpStatus.OK),

    PARAM_ERROR(10001, "参数校验失败", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(10002, "未登录或登录已失效", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(10003, "没有操作权限", HttpStatus.FORBIDDEN),
    NOT_FOUND(10004, "资源不存在", HttpStatus.NOT_FOUND),
    CONFLICT(10005, "数据冲突", HttpStatus.CONFLICT),
    TOO_MANY_REQUESTS(10006, "请求过于频繁，请稍后再试", HttpStatus.TOO_MANY_REQUESTS),

    USERNAME_EXISTS(20001, "用户名已被占用", HttpStatus.CONFLICT),
    USER_NOT_FOUND(20002, "用户不存在", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(20003, "用户名或密码错误", HttpStatus.UNAUTHORIZED),
    ACCOUNT_DISABLED(20004, "账号已被禁用", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(20005, "令牌无效", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(20006, "令牌已过期", HttpStatus.UNAUTHORIZED),

    FILE_EMPTY(30001, "上传文件不能为空", HttpStatus.BAD_REQUEST),
    FILE_TYPE_NOT_ALLOWED(30002, "不支持的文件类型", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    FILE_TOO_LARGE(30003, "文件超出大小限制", HttpStatus.PAYLOAD_TOO_LARGE),

    STORAGE_NOT_CONFIGURED(40001, "对象存储未配置，请先配置阿里云 OSS", HttpStatus.SERVICE_UNAVAILABLE),
    STORAGE_UPLOAD_FAILED(40002, "文件上传失败，请稍后重试", HttpStatus.BAD_GATEWAY),

    SYSTEM_ERROR(50001, "系统繁忙，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
