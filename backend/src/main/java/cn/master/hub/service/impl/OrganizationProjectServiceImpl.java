package cn.master.hub.service.impl;

import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.OrganizationProjectRequest;
import cn.master.hub.dto.system.UpdateProjectRequest;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.service.OrganizationProjectService;
import cn.master.hub.service.SystemProjectService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.hub.entity.table.SystemOrganizationTableDef.SYSTEM_ORGANIZATION;
import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * @author Created by 11's papa on 2025/9/10
 */
@Service
@RequiredArgsConstructor
public class OrganizationProjectServiceImpl implements OrganizationProjectService {
    private final SystemProjectService projectService;
    private final static String PREFIX = "/organization-project";
    private final static String ADD_PROJECT = PREFIX + "/add";
    private final static String UPDATE_PROJECT = PREFIX + "/update";
    private final static String REMOVE_PROJECT_MEMBER = PREFIX + "/remove-member/";
    private final static String ADD_MEMBER = PREFIX + "/add-member";

    @Override
    public ProjectDTO add(AddProjectRequest addProjectDTO, String createUser) {
        return projectService.add(addProjectDTO, createUser, ADD_PROJECT, OperationLogModule.SETTING_ORGANIZATION_PROJECT);
    }

    @Override
    public Page<ProjectDTO> getProjectList(OrganizationProjectRequest request) {
        Page<ProjectDTO> page = QueryChain.of(SystemProject.class)
                .select(SYSTEM_PROJECT.ALL_COLUMNS, SYSTEM_ORGANIZATION.NAME.as("organizationName"))
                .from(SYSTEM_PROJECT).innerJoin(SYSTEM_ORGANIZATION).on(SYSTEM_PROJECT.ORGANIZATION_ID.eq(SYSTEM_ORGANIZATION.ID))
                .where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(request.getOrganizationId()))
                .and(SYSTEM_PROJECT.NUM.like(request.getKeyword()).or(SYSTEM_PROJECT.NAME.like(request.getKeyword())))
                .orderBy(SYSTEM_PROJECT.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), ProjectDTO.class);
        return projectService.buildUserInfo(page);
    }

    @Override
    public ProjectDTO update(UpdateProjectRequest request, String updateUser) {
        return projectService.update(request, updateUser, UPDATE_PROJECT, OperationLogModule.SETTING_ORGANIZATION_PROJECT);
    }

    @Override
    public List<UserExtendDTO> getUserAdminList(String organizationId, String keyword) {
        checkOrgIsExist(organizationId);
        return QueryChain.of(SystemUser.class)
                .select(QueryMethods.distinct(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.EMAIL,SYSTEM_USER.CREATE_TIME))
                .from(SYSTEM_USER).leftJoin(USER_ROLE_RELATION).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId))
                .and(SYSTEM_USER.NAME.like(keyword).or(SYSTEM_USER.EMAIL.like(keyword)))
                .orderBy(SYSTEM_USER.CREATE_TIME.desc()).limit(1000)
                .listAs(UserExtendDTO.class);
    }

    @Override
    public int delete(String id, String deleteUser) {
        return projectService.delete(id, deleteUser);
    }

    @Override
    public void enable(String id, String updateUser) {
        projectService.enable(id, updateUser);
    }

    @Override
    public void disable(String id, String updateUser) {
        projectService.disable(id, updateUser);
    }

    private void checkOrgIsExist(String organizationId) {
        boolean exists = QueryChain.of(SystemOrganization.class).where(SYSTEM_ORGANIZATION.ID.eq(organizationId)).exists();
        if (!exists) {
            throw new CustomException(Translator.get("organization_not_exists"));
        }
    }
}
