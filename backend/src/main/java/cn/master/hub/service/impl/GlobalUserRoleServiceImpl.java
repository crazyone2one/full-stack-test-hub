package cn.master.hub.service.impl;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.service.GlobalUserRoleService;
import com.mybatisflex.core.BaseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/12
 */
@Service
public class GlobalUserRoleServiceImpl implements GlobalUserRoleService {
    @Override
    public BaseMapper<UserRole> getMapper() {
        return null;
    }

    @Override
    public List<UserRole> list() {
        return List.of();
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(UserRole userRole) {
        return List.of();
    }

    @Override
    public UserRole add(UserRole userRole) {
        return null;
    }

    @Override
    public void delete(UserRole userRole, String defaultRoleId, String currentUserId, String orgId) {

    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {

    }

    @Override
    public void checkRoleIsGlobalAndHaveMember(List<String> roleIdList, boolean isSystem) {

    }
}
