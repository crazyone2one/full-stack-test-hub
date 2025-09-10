package cn.master.hub.service.impl;

import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.OrganizationProjectRequest;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.service.OrganizationProjectService;
import cn.master.hub.service.SystemProjectService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static cn.master.hub.entity.table.SystemOrganizationTableDef.SYSTEM_ORGANIZATION;
import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;

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
}
