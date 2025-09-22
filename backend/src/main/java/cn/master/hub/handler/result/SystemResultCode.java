package cn.master.hub.handler.result;

import cn.master.hub.handler.exception.IResultCode;

/**
 * @author Created by 11's papa on 2025/9/22
 */
public enum SystemResultCode implements IResultCode {
    USER_TOO_MANY(101511, "User too many"),
    DEPT_USER_TOO_MANY(101512, "Department user too many"),
    INVITE_EMAIL_EXIST(101513, "user_email_already_exists"),;

    private final int code;
    private final String message;

    SystemResultCode(int code, String message) {
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
