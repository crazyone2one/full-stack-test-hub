package cn.master.hub.controller;

import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.OrganizationProjectRequest;
import cn.master.hub.dto.system.UpdateProjectRequest;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.log.annotation.Log;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import cn.master.hub.service.OrganizationProjectService;
import cn.master.hub.service.log.OrganizationProjectLogService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Created by 11's papa on 2025/9/10
 */
@RestController
@Tag(name = "系统设置-组织-项目")
@RequiredArgsConstructor
@RequestMapping("/organization/project")
public class OrganizationProjectController {
    private final OrganizationProjectService organizationProjectService;

    @PostMapping("/add")
    @Operation(summary = "系统设置-组织-项目-创建项目")
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = OrganizationProjectLogService.class)
    public ProjectDTO addProject(@RequestBody @Validated({Created.class}) AddProjectRequest request) {
        return organizationProjectService.add(request, SessionUtils.getCurrentUserName());
    }

    @PostMapping("/page")
    @Operation(summary = "系统设置-组织-项目-获取项目列表")
    public Page<ProjectDTO> getProjectList(@Validated @RequestBody OrganizationProjectRequest request) {
        return organizationProjectService.getProjectList(request);
    }

    @PostMapping("/update")
    @Operation(summary = "系统设置-组织-项目-编辑")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#request)", msClass = OrganizationProjectLogService.class)
    public ProjectDTO updateProject(@RequestBody @Validated({Updated.class}) UpdateProjectRequest request) {
        return organizationProjectService.update(request, SessionUtils.getCurrentUserName());
    }
}
