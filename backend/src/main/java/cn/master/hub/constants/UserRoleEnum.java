package cn.master.hub.constants;

/**
 * @author Created by 11's papa on 2025/9/12
 */
public enum UserRoleEnum {
    GLOBAL("global");

    private final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}

