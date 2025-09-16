package cn.master.hub.service.impl;

import cn.master.hub.constants.UserRoleEnum;
import cn.master.hub.constants.UserRoleType;
import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.handler.PermissionCache;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.service.BaseUserRolePermissionService;
import cn.master.hub.service.BaseUserRoleRelationService;
import cn.master.hub.service.OrganizationUserRoleService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static cn.master.hub.handler.result.ResultCode.NO_ORG_USER_ROLE_PERMISSION;

/**
 * @author Created by 11's papa on 2025/9/15
 */
@Service
public class OrganizationUserRoleServiceImpl extends BaseUserRoleServiceImpl implements OrganizationUserRoleService {
    public OrganizationUserRoleServiceImpl(BaseUserRolePermissionService baseUserRolePermissionService,
                                           BaseUserRoleRelationService baseUserRoleRelationService,
                                           PermissionCache permissionCache) {
        super(baseUserRolePermissionService, baseUserRoleRelationService, permissionCache);
    }

    @Override
    public List<UserRole> list(String organizationId) {
        List<UserRole> userRoles = queryChain()
                .where(UserRole::getType).eq(UserRoleType.ORGANIZATION.name())
                .and(UserRole::getScopeId).in(Arrays.asList(organizationId, UserRoleEnum.GLOBAL.toString()))
                .orderBy(UserRole::getCreateTime).desc()
                .list();
        userRoles.sort(Comparator.comparing(UserRole::getInternal).thenComparing(UserRole::getScopeId)
                .thenComparing(Comparator.comparing(UserRole::getCreateTime).thenComparing(UserRole::getId).reversed()).reversed());
        return userRoles;

    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(String id) {
        UserRole userRole = get(id);
        checkOrgUserRole(userRole);
        return getPermissionSetting(userRole);
    }

    private void checkOrgUserRole(UserRole userRole) {
        if (!UserRoleType.ORGANIZATION.name().equals(userRole.getType())) {
            throw new CustomException(NO_ORG_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        UserRole userRole = get(request.getUserRoleId());
        checkOrgUserRole(userRole);
        checkGlobalUserRole(userRole);
        super.updatePermissionSetting(request);
    }
}
