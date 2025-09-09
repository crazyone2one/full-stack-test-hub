package cn.master.hub.service;

import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.entity.UserRolePermission;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 用户组权限 服务层。
 *
 * @author 11's papa
 * @since 2025-09-09
 */
public interface BaseUserRolePermissionService extends IService<UserRolePermission> {
    List<UserRolePermission> getByRoleId(String roleId);

    Set<String> getPermissionIdSetByRoleId(String roleId);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);

    void deleteByRoleId(String roleId);
}
