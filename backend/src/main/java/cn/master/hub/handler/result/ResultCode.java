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
    /**
     * 调用获取全局用户组接口，如果操作的是非全局的用户组，会返回该响应码
     */
    GLOBAL_USER_ROLE_PERMISSION(101001, "global_user_role_permission_error"),
    GLOBAL_USER_ROLE_EXIST(101002, "global_user_role_exist_error"),
    GLOBAL_USER_ROLE_RELATION_SYSTEM_PERMISSION(101003, "global_user_role_relation_system_permission_error"),
    GLOBAL_USER_ROLE_LIMIT(101004, "global_user_role_limit_error"),
    SERVICE_INTEGRATION_EXIST(101005, "service_integration_exist_error"),

    NO_ORG_USER_ROLE_PERMISSION(101007, "organization_user_role_permission_error"),
    ORGANIZATION_TEMPLATE_PERMISSION(101009, "organization_template_permission_error"),
    PLUGIN_SCRIPT_EXIST(101010, "plugin.script.exist"),
    PLUGIN_SCRIPT_FORMAT(101011, "plugin.script.format"),
    NO_PROJECT_USER_ROLE_PERMISSION(101012, "project_user_role_permission_error"),
    NO_GLOBAL_USER_ROLE_PERMISSION(101013, "no_global_user_role_permission_error"),
    PLUGIN_PARSE_ERROR(101014, "plugin.parse.error"),
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
