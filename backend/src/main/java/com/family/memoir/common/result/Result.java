package com.family.memoir.common.result;

import com.family.memoir.common.exception.ErrorCode;
import org.slf4j.MDC;

/**
 * 统一响应体。traceId 取自 MDC，便于按请求链路排查。
 */
public record Result<T>(int code, String message, T data, String traceId) {

    public static <T> Result<T> ok(T data) {
        return new Result<>(ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), data, currentTraceId());
    }

    public static Result<Void> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> Result<T> fail(ErrorCode errorCode, String message) {
        return fail(errorCode.getCode(), message);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null, currentTraceId());
    }

    private static String currentTraceId() {
        return MDC.get("traceId");
    }
}
