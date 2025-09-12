package cn.master.hub.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/12
 */
public interface GlobalUserRoleService extends BaseUserRoleService {
    void checkRoleIsGlobalAndHaveMember(@Valid @NotEmpty List<String> roleIdList, boolean isSystem);
}
