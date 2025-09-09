package cn.master.hub.service;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.entity.UserRole;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户组 服务层。
 *
 * @author 11's papa
 * @since 2025-09-09
 */
public interface BaseUserRoleService extends IService<UserRole> {
    List<UserRole> list();

    List<PermissionDefinitionItem> getPermissionSetting(UserRole userRole);

    UserRole add(UserRole userRole);

    void delete(UserRole userRole, String defaultRoleId, String currentUserId, String orgId);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);
}
