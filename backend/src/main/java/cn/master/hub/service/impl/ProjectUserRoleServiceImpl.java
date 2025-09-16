package cn.master.hub.service.impl;

import cn.master.hub.constants.UserRoleType;
import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.handler.PermissionCache;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.service.BaseUserRolePermissionService;
import cn.master.hub.service.BaseUserRoleRelationService;
import cn.master.hub.service.ProjectUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.hub.handler.result.ResultCode.NO_PROJECT_USER_ROLE_PERMISSION;

/**
 * @author Created by 11's papa on 2025/9/15
 */
@Service
public class ProjectUserRoleServiceImpl extends BaseUserRoleServiceImpl implements ProjectUserRoleService {
    public ProjectUserRoleServiceImpl(BaseUserRolePermissionService baseUserRolePermissionService,
                                      BaseUserRoleRelationService baseUserRoleRelationService,
                                      PermissionCache permissionCache) {
        super(baseUserRolePermissionService, baseUserRoleRelationService, permissionCache);
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(String id) {
        UserRole userRole = mapper.selectOneById(id);
        checkProjectUserRole(userRole);
        return getPermissionSetting(userRole);
    }

    private void checkProjectUserRole(UserRole userRole) {
        if (!UserRoleType.PROJECT.name().equals(userRole.getType())) {
            throw new CustomException(NO_PROJECT_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        UserRole userRole = get(request.getUserRoleId());
        checkProjectUserRole(userRole);
        checkGlobalUserRole(userRole);
        super.updatePermissionSetting(request);
    }
}
