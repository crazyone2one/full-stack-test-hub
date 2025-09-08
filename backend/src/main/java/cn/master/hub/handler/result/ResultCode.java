package cn.master.hub.handler.result;

import cn.master.hub.handler.exception.IResultCode;

/**
 * @author Created by 11's papa on 2025/8/29
 */
public enum ResultCode implements IResultCode {
    SUCCESS(100200, "http_result_success"),
    FAILED(100500, "http_result_unknown_exception"),
    VALIDATE_FAILED(100400, "http_result_validate"),
    UNAUTHORIZED(100401, "http_result_unauthorized"),
    FORBIDDEN(100403, "http_result_forbidden"),
    NOT_FOUND(100404, "http_result_not_found"),

    INVITE_EMAIL_EXIST(101513, "user_email_already_exists"),
//    METHOD_NOT_ALLOWED(100405, "请求方法不允许"),
//    REQUEST_TIMEOUT(100408, "请求超时"),
//    CONFLICT(100409, "资源冲突"),
//    UNSUPPORTED_MEDIA_TYPE(100415, "不支持的媒体类型"),
    ;
    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return getTranslationMessage(this.message);
    }
}
