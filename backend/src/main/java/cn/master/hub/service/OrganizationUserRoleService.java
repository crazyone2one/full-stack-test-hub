package cn.master.hub.service;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.dto.system.request.OrganizationUserRoleMemberEditRequest;
import cn.master.hub.dto.system.request.OrganizationUserRoleMemberRequest;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/15
 */
public interface OrganizationUserRoleService extends BaseUserRoleService {
    List<UserRole> list(String organizationId);

    List<PermissionDefinitionItem> getPermissionSetting(String id);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);

    Page<SystemUser> listMember(OrganizationUserRoleMemberRequest request);

    void addMember(OrganizationUserRoleMemberEditRequest request, String operator);

    void removeMember(OrganizationUserRoleMemberEditRequest request);
}
