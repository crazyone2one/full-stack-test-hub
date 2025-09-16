package cn.master.hub.service;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.project.ProjectUserRoleDTO;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.dto.request.project.ProjectUserRoleMemberEditRequest;
import cn.master.hub.dto.request.project.ProjectUserRoleMemberRequest;
import cn.master.hub.dto.request.project.ProjectUserRoleRequest;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/15
 */
public interface ProjectUserRoleService extends BaseUserRoleService {
    List<PermissionDefinitionItem> getPermissionSetting(String id);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);

    Page<ProjectUserRoleDTO> page(ProjectUserRoleRequest request);

    UserRole add(UserRole userRole);

    UserRole update(UserRole userRole);

    void delete(String roleId, String currentUserId);

    Page<SystemUser> listMember(ProjectUserRoleMemberRequest request);

    void addMember(ProjectUserRoleMemberEditRequest request, String currentUserName);

    void removeMember(ProjectUserRoleMemberEditRequest request);
}
