package cn.master.hub.service;

import cn.master.hub.constants.UserRoleType;
import cn.master.hub.dto.UserDTO;
import cn.master.hub.dto.UserRolePermissionDTO;
import cn.master.hub.dto.request.AuthenticationRequest;
import cn.master.hub.dto.request.RefreshTokenRequest;
import cn.master.hub.dto.response.AuthenticationResponse;
import cn.master.hub.entity.*;
import cn.master.hub.handler.JwtTokenProvider;
import cn.master.hub.handler.result.ResultHolder;
import com.mybatisflex.core.query.QueryChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.SystemOrganizationTableDef.SYSTEM_ORGANIZATION;
import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;

/**
 * @author Created by 11's papa on 2025/8/29
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final SystemUserService systemUserService;
    private final AuthenticationUserService authenticationUserService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticate);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.username());
        String accessToken = jwtTokenProvider.generateToken(request.username(), "access_token");
//        String refreshToken = jwtTokenProvider.generateToken(authenticate, "refresh_token");
//        UserResponse userResponse = QueryChain.of(SystemUser.class)
//                .select(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.EMAIL, SYSTEM_USER.LAST_ORGANIZATION_ID, SYSTEM_USER.LAST_PROJECT_ID, SYSTEM_USER.AVATAR)
//                .from(SYSTEM_USER).where(SYSTEM_USER.NAME.eq(authenticate.getName()))
//                .oneAs(UserResponse.class);
        UserDTO user = getUser(authenticate.getName());
        autoSwitch(user);
        return new AuthenticationResponse(accessToken, refreshToken.getToken(), user);
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    String accessToken = jwtTokenProvider.generateToken(userId, "access_token");
                    return new AuthenticationResponse(accessToken, refreshTokenRequest.refreshToken(), null);
                }).orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
    }

    public ResultHolder logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            // Logs out the user by clearing their authentication info from the SecurityContext
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        tokenBlacklistService.addToBlacklist(request);
        return ResultHolder.success("Logout successful");
    }

    private UserDTO getUser(String username) {
        UserDTO userDTO = QueryChain.of(SystemUser.class).where(SystemUser::getName).eq(username).oneAs(UserDTO.class);
        if (userDTO == null) {
            return null;
        }
        UserRolePermissionDTO dto = authenticationUserService.getUserRolePermission(userDTO.getId());
        userDTO.setUserRoleRelations(dto.getUserRoleRelations());
        userDTO.setUserRoles(dto.getUserRoles());
        userDTO.setUserRolePermissions(dto.getList());
        return userDTO;
    }

    public void autoSwitch(UserDTO user) {
        // 判断是否是系统管理员
        if (isSystemAdmin(user)) {
            return;
        }
        // 用户有 last_project_id 权限
        if (hasLastProjectPermission(user)) {
            return;
        }
        // 用户有 last_organization_id 权限
        if (hasLastOrganizationPermission(user)) {
            return;
        }
        // 判断其他权限
        checkNewOrganizationAndProject(user);
    }

    private void checkNewOrganizationAndProject(UserDTO user) {
        List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations();
        List<String> projectRoleIds = user.getUserRoles()
                .stream().filter(ug -> Strings.CS.equals(ug.getType(), UserRoleType.PROJECT.name()))
                .map(UserRole::getId)
                .toList();
        List<UserRoleRelation> project = userRoleRelations.stream().filter(ug -> projectRoleIds.contains(ug.getRoleId()))
                .toList();
        if (CollectionUtils.isEmpty(project)) {
            List<String> organizationIds = user.getUserRoles()
                    .stream()
                    .filter(ug -> Strings.CS.equals(ug.getType(), UserRoleType.ORGANIZATION.name()))
                    .map(UserRole::getId)
                    .toList();
            List<UserRoleRelation> organizations = userRoleRelations.stream().filter(ug -> organizationIds.contains(ug.getRoleId()))
                    .toList();
            if (!organizations.isEmpty()) {
                //获取所有的组织
                List<String> orgIds = organizations.stream().map(UserRoleRelation::getSourceId).collect(Collectors.toList());
                List<SystemOrganization> organizationsList = QueryChain.of(SystemOrganization.class)
                        .where(SystemOrganization::getId).in(orgIds)
                        .and(SystemOrganization::getEnable).eq(true).list();
                if (!organizationsList.isEmpty()) {
                    String wsId = organizationsList.getFirst().getId();
                    authenticationUserService.switchUserResource(wsId, user);
                }
            } else {
                // 用户登录之后没有项目和组织的权限就把值清空
                user.setLastOrganizationId(StringUtils.EMPTY);
                user.setLastProjectId(StringUtils.EMPTY);
                systemUserService.updateUser(user);
            }
        } else {
            UserRoleRelation userRoleRelation = project.stream().filter(p -> StringUtils.isNotBlank(p.getSourceId()))
                    .toList().getFirst();
            String projectId = userRoleRelation.getSourceId();
            SystemProject p = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(projectId).one();
            String wsId = p.getOrganizationId();
            user.setId(user.getId());
            user.setLastProjectId(projectId);
            user.setLastOrganizationId(wsId);
            systemUserService.updateUser(user);
        }
    }

    private boolean hasLastOrganizationPermission(UserDTO user) {
        if (StringUtils.isNotBlank(user.getLastOrganizationId())) {
            List<SystemOrganization> organizations = QueryChain.of(SystemOrganization.class)
                    .where(SystemOrganization::getId).eq(user.getLastOrganizationId())
                    .and(SystemOrganization::getEnable).eq(true).list();
            if (CollectionUtils.isEmpty(organizations)) {
                return false;
            }
            List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations().stream()
                    .filter(ug -> Strings.CS.equals(user.getLastOrganizationId(), ug.getSourceId()))
                    .toList();
            if (!userRoleRelations.isEmpty()) {
                List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(user.getLastOrganizationId())
                        .and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
                // 组织下没有项目
                if (CollectionUtils.isEmpty(projects)) {
                    user.setLastProjectId(StringUtils.EMPTY);
                    systemUserService.updateUser(user);
                    return true;
                }
                // 组织下有项目，选中有权限的项目
                List<String> projectIds = projects.stream()
                        .map(SystemProject::getId)
                        .toList();
                List<UserRoleRelation> roleRelations = user.getUserRoleRelations();
                List<String> projectRoleIds = user.getUserRoles()
                        .stream().filter(ug -> Strings.CS.equals(ug.getType(), UserRoleType.PROJECT.name()))
                        .map(UserRole::getId)
                        .toList();
                List<String> projectIdsWithPermission = roleRelations.stream().filter(ug -> projectRoleIds.contains(ug.getRoleId()))
                        .map(UserRoleRelation::getSourceId)
                        .filter(StringUtils::isNotBlank)
                        .filter(projectIds::contains)
                        .toList();
                List<String> intersection = projectIds.stream().filter(projectIdsWithPermission::contains).toList();
                // 当前组织下的所有项目都没有权限
                if (CollectionUtils.isEmpty(intersection)) {
                    user.setLastProjectId(StringUtils.EMPTY);
                    systemUserService.updateUser(user);
                    return true;
                }
                Optional<SystemProject> first = projects.stream().filter(p -> Strings.CS.equals(intersection.getFirst(), p.getId())).findFirst();
                if (first.isPresent()) {
                    SystemProject project = first.get();
                    String wsId = project.getOrganizationId();
                    user.setId(user.getId());
                    user.setLastProjectId(project.getId());
                    user.setLastOrganizationId(wsId);
                    systemUserService.updateUser(user);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasLastProjectPermission(UserDTO user) {
        if (StringUtils.isNotBlank(user.getLastProjectId())) {
            List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations().stream()
                    .filter(ug -> Strings.CS.equals(user.getLastProjectId(), ug.getSourceId()))
                    .toList();
            if (!userRoleRelations.isEmpty()) {
                List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ID.eq(user.getLastProjectId())
                        .and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
                if (!projects.isEmpty()) {
                    SystemProject project = projects.getFirst();
                    if (Strings.CS.equals(project.getOrganizationId(), user.getLastOrganizationId())) {
                        return true;
                    }
                    // last_project_id 和 last_organization_id 对应不上了
                    user.setLastOrganizationId(project.getOrganizationId());
                    systemUserService.updateUser(user);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSystemAdmin(UserDTO user) {
        if (systemUserService.isSuperUser(user.getId())) {
            if (StringUtils.isNotBlank(user.getLastProjectId())) {
                List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(user.getLastProjectId())
                        .and(SystemProject::getEnable).eq(true).list();
                if (!projects.isEmpty()) {
                    SystemProject project = projects.getFirst();
                    if (Strings.CS.equals(project.getOrganizationId(), user.getLastOrganizationId())) {
                        return true;
                    }
                    // last_project_id 和 last_organization_id 对应不上了
                    user.setLastOrganizationId(project.getOrganizationId());
                    systemUserService.updateUser(user);
                    return true;
                }
            }
            if (StringUtils.isNotBlank(user.getLastOrganizationId())) {
                List<SystemOrganization> organizations = QueryChain.of(SystemOrganization.class).where(SystemOrganization::getId).eq(user.getLastOrganizationId())
                        .and(SystemOrganization::getEnable).eq(true).list();
                if (!organizations.isEmpty()) {
                    SystemOrganization organization = organizations.getFirst();
                    List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SystemProject::getOrganizationId).eq(organization.getId())
                            .and(SystemProject::getEnable).eq(true).list();
                    if (!projects.isEmpty()) {
                        SystemProject project = projects.getFirst();
                        user.setLastProjectId(project.getId());
                    }
                    systemUserService.updateUser(user);
                    return true;
                }
            }
            //项目和组织都没有权限
            SystemProject project = getEnableProjectAndOrganization();
            if (project != null) {
                user.setLastProjectId(project.getId());
                user.setLastOrganizationId(project.getOrganizationId());
                systemUserService.updateUser(user);
                return true;
            }
            return true;
        }
        return false;
    }

    private SystemProject getEnableProjectAndOrganization() {
        return QueryChain.of(SystemProject.class)
                .select(SYSTEM_PROJECT.ALL_COLUMNS)
                .from(SYSTEM_PROJECT)
                .leftJoin(SYSTEM_ORGANIZATION).on(SYSTEM_PROJECT.ORGANIZATION_ID.eq(SYSTEM_ORGANIZATION.ID))
                .where(SYSTEM_PROJECT.ENABLE.eq(true).and(SYSTEM_ORGANIZATION.ENABLE.eq(true)))
                .limit(1)
                .one();
    }
}
