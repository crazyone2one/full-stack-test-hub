package cn.master.hub.service.impl;

import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.entity.UserRolePermission;
import cn.master.hub.mapper.UserRolePermissionMapper;
import cn.master.hub.service.BaseUserRolePermissionService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.UserRolePermissionTableDef.USER_ROLE_PERMISSION;

/**
 * 用户组权限 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-09
 */
@Service
public class BaseUserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission> implements BaseUserRolePermissionService {

    @Override
    public List<UserRolePermission> getByRoleId(String roleId) {
        return queryChain().where(UserRolePermission::getRoleId).eq(roleId).list();
    }

    @Override
    public Set<String> getPermissionIdSetByRoleId(String roleId) {
        return getByRoleId(roleId).stream()
                .map(UserRolePermission::getPermissionId)
                .collect(Collectors.toSet());
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        List<PermissionSettingUpdateRequest.PermissionUpdateRequest> permissions = request.getPermissions();
        QueryWrapper query = queryChain().where(UserRolePermission::getRoleId).eq(request.getUserRoleId())
                .and(UserRolePermission::getPermissionId).ne("PROJECT_BASE_INFO:READ");
        mapper.deleteByQuery(query);

        String groupId = request.getUserRoleId();
        permissions.forEach(permission -> {
            if (BooleanUtils.isTrue(permission.getEnable())) {
                String permissionId = permission.getId();
                UserRolePermission groupPermission = new UserRolePermission();
                groupPermission.setRoleId(groupId);
                groupPermission.setPermissionId(permissionId);
                mapper.insert(groupPermission);
            }
        });
    }

    @Override
    public void deleteByRoleId(String roleId) {
        mapper.deleteByCondition(USER_ROLE_PERMISSION.ROLE_ID.eq(roleId));
    }
}
