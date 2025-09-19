package cn.master.hub.handler.exception;

/**
 * @author Created by 11's papa on 2025/9/19
 */
public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
