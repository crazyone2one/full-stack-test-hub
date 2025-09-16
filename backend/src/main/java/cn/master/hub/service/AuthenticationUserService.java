package cn.master.hub.service;

import cn.master.hub.dto.UserDTO;
import cn.master.hub.dto.UserRolePermissionDTO;
import cn.master.hub.dto.UserRoleResourceDTO;
import cn.master.hub.entity.*;
import cn.master.hub.handler.security.UserPrincipal;
import cn.master.hub.mapper.SystemUserMapper;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;

/**
 * @author Created by 11's papa on 2025/9/16
 */
@Service
@RequiredArgsConstructor
public class AuthenticationUserService {
    private final SystemUserMapper systemUserMapper;

    public UserDTO getUserDTO(String userId) {
        UserDTO userDTO = QueryChain.of(SystemUser.class).where(SystemUser::getId).eq(userId).oneAs(UserDTO.class);
        if (userDTO == null) {
            return null;
        }
        UserRolePermissionDTO dto = getUserRolePermission(userId);
        userDTO.setUserRoleRelations(dto.getUserRoleRelations());
        userDTO.setUserRoles(dto.getUserRoles());
        userDTO.setUserRolePermissions(dto.getList());
        return userDTO;
    }

    public UserRolePermissionDTO getUserRolePermission(String userId) {
        UserRolePermissionDTO permissionDTO = new UserRolePermissionDTO();
        List<UserRoleResourceDTO> list = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class)
                .where(UserRoleRelation::getUserId).eq(userId).list();
        if (CollectionUtils.isEmpty(userRoleRelations)) {
            return permissionDTO;
        }
        permissionDTO.setUserRoleRelations(userRoleRelations);
        List<String> roleIdList = userRoleRelations.stream().map(UserRoleRelation::getRoleId).toList();
        List<UserRole> userRoles = QueryChain.of(UserRole.class).where(UserRole::getId).in(roleIdList).list();
        permissionDTO.setUserRoles(userRoles);
        for (UserRole gp : userRoles) {
            UserRoleResourceDTO dto = new UserRoleResourceDTO();
            dto.setUserRole(gp);
            List<UserRolePermission> userRolePermissions = QueryChain.of(UserRolePermission.class)
                    .where(UserRolePermission::getRoleId).eq(gp.getId())
                    .list();
            dto.setUserRolePermissions(userRolePermissions);
            list.add(dto);
        }
        permissionDTO.setList(list);
        return permissionDTO;
    }

    public void switchUserResource(String sourceId, UserDTO u) {
        UserDTO user = getUserDTO(u.getId());
        SystemUser newUser = new SystemUser();
        user.setLastOrganizationId(sourceId);
        user.setLastProjectId(StringUtils.EMPTY);
        List<SystemProject> projects = getProjectListByWsAndUserId(u.getId(), sourceId);
        if (!projects.isEmpty()) {
            user.setLastProjectId(projects.getFirst().getId());
        }
        BeanUtils.copyProperties(user, newUser);
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 创建新的 UserPrincipal 对象
        UserPrincipal updatedPrincipal = new UserPrincipal(newUser);
        // 创建新的 Authentication 对象
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updatedPrincipal,
                authentication.getCredentials(),
                updatedPrincipal.getAuthorities()
        );
        // 更新安全上下文
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        systemUserMapper.update(newUser);
    }

    private List<SystemProject> getProjectListByWsAndUserId(String userId, String sourceId) {
        List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(sourceId)
                .and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(UserRoleRelation::getUserId).eq(userId).list();
        List<SystemProject> projectList = new ArrayList<>();
        userRoleRelations.forEach(userRoleRelation -> projects.forEach(project -> {
            if (Strings.CS.equals(userRoleRelation.getSourceId(), project.getId())) {
                if (!projectList.contains(project)) {
                    projectList.add(project);
                }
            }
        }));
        return projectList;
    }
}
