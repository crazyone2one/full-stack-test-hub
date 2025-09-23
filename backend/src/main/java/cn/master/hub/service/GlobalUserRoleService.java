package cn.master.hub.service;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.entity.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/12
 */
public interface GlobalUserRoleService extends BaseUserRoleService {
    void checkRoleIsGlobalAndHaveMember(@Valid @NotEmpty List<String> roleIdList, boolean isSystem);

    UserRole add(UserRole userRole);

    UserRole update(UserRole userRole);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);

    List<PermissionDefinitionItem> getPermissionSetting(String id);

    void checkGlobalUserRole(UserRole userRole);

    void delete(String id, String currentUserId);

    List<UserRole> getList(List<String> checkIdList);

    void checkSystemUserGroup(UserRole userRole);
}
