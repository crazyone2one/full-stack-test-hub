package cn.master.hub.service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Created by 11's papa on 2025/9/4
 */
public interface TokenBlacklistService {
    void addToBlacklist(HttpServletRequest request);

    Boolean isBlacklisted(String token);
}
