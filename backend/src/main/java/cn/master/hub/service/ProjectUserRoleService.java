package cn.master.hub.service;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/15
 */
public interface ProjectUserRoleService extends BaseUserRoleService {
    List<PermissionDefinitionItem> getPermissionSetting(String id);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);
}
