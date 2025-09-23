package cn.master.hub.service.impl;

import cn.master.hub.constants.UserRoleScope;
import cn.master.hub.constants.UserRoleType;
import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.handler.PermissionCache;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.service.BaseUserRolePermissionService;
import cn.master.hub.service.BaseUserRoleRelationService;
import cn.master.hub.service.GlobalUserRoleService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.master.hub.constants.InternalUserRole.MEMBER;
import static cn.master.hub.entity.table.UserRoleTableDef.USER_ROLE;
import static cn.master.hub.handler.result.ResultCode.*;

/**
 * @author Created by 11's papa on 2025/9/12
 */
@Service("globalUserRoleService")
public class GlobalUserRoleServiceImpl extends BaseUserRoleServiceImpl implements GlobalUserRoleService {


    public GlobalUserRoleServiceImpl(BaseUserRolePermissionService baseUserRolePermissionService,
                                     BaseUserRoleRelationService baseUserRoleRelationService,
                                     PermissionCache permissionCache) {
        super(baseUserRolePermissionService, baseUserRoleRelationService, permissionCache);
    }

    @Override
    public UserRole add(UserRole userRole) {
        userRole.setInternal(false);
        userRole.setScopeId(UserRoleScope.GLOBAL);
        checkExist(userRole);
        return super.add(userRole);
    }

    @Override
    public UserRole update(UserRole userRole) {
        UserRole originUserRole = getWithCheck(userRole.getId());
        checkGlobalUserRole(originUserRole);
        checkInternalUserRole(originUserRole);
        userRole.setInternal(false);
        checkExist(userRole);
        return super.update(userRole);
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        UserRole userRole = getWithCheck(request.getUserRoleId());
        checkGlobalUserRole(userRole);
        // 内置管理员级别用户组无法更改权限
        checkAdminUserRole(userRole);
        super.updatePermissionSetting(request);
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(String id) {
        UserRole userRole = getWithCheck(id);
        checkGlobalUserRole(userRole);
        return getPermissionSetting(userRole);
    }

    @Override
    public void checkGlobalUserRole(UserRole userRole) {
        if (!Strings.CS.equals(userRole.getScopeId(), UserRoleScope.GLOBAL)) {
            throw new CustomException(GLOBAL_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public void delete(String id, String currentUserId) {
        UserRole userRole = getWithCheck(id);
        checkGlobalUserRole(userRole);
        super.delete(userRole, MEMBER.getValue(), currentUserId, UserRoleScope.SYSTEM);
    }

    @Override
    public List<UserRole> getList(List<String> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        } else {
            return mapper.selectListByIds(idList);
        }
    }

    @Override
    public void checkSystemUserGroup(UserRole userRole) {
        if (!Strings.CS.equals(userRole.getType(), UserRoleType.SYSTEM.name())) {
            throw new CustomException(GLOBAL_USER_ROLE_RELATION_SYSTEM_PERMISSION);
        }
    }

    private void checkExist(UserRole userRole) {
        if (StringUtils.isBlank(userRole.getName())) {
            return;
        }
        List<UserRole> userRoles = queryChain().where(USER_ROLE.NAME.eq(userRole.getName())
                .and(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL))
                .and(USER_ROLE.ID.ne(userRole.getId()).when(StringUtils.isNoneBlank(userRole.getId())))).list();
        if (!userRoles.isEmpty()) {
            throw new CustomException(GLOBAL_USER_ROLE_EXIST);
        }
    }

    @Override
    public void checkRoleIsGlobalAndHaveMember(List<String> roleIdList, boolean isSystem) {
        List<String> globalRoleList = queryChain().select(USER_ROLE.ID).from(USER_ROLE)
                .where(USER_ROLE.TYPE.eq("SYSTEM").when(isSystem)).and(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL))
                .listAs(String.class);
        if (globalRoleList.size() != roleIdList.size()) {
            throw new CustomException(Translator.get("role.not.global"));
        }
    }
}
