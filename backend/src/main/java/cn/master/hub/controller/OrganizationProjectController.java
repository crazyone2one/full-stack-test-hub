package cn.master.hub.controller;

import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.OrganizationProjectRequest;
import cn.master.hub.dto.system.UpdateProjectRequest;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.log.annotation.Log;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import cn.master.hub.service.OrganizationProjectService;
import cn.master.hub.service.log.OrganizationProjectLogService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/user-admin-list/{organizationId}")
    @Operation(summary = "系统设置-组织-项目-获取项目管理员下拉选项")
    public List<UserExtendDTO> getUserAdminList(@PathVariable String organizationId, @Schema(description = "查询关键字，根据邮箱和用户名查询")
    @RequestParam(value = "keyword", required = false) String keyword) {
        return organizationProjectService.getUserAdminList(organizationId, keyword);
    }

    @GetMapping("/delete/{id}")
    @Operation(summary = "系统设置-组织-项目-删除")
    @Parameter(name = "id", description = "项目", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#id)", msClass = OrganizationProjectLogService.class)
    public int deleteProject(@PathVariable String id) {
        return organizationProjectService.delete(id, SessionUtils.getCurrentUserName());
    }

    @GetMapping("/enable/{id}")
    @Operation(summary = "系统设置-组织-项目-启用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#id)", msClass = OrganizationProjectLogService.class)
    public void enable(@PathVariable String id) {
        organizationProjectService.enable(id, SessionUtils.getCurrentUserName());
    }

    @GetMapping("/disable/{id}")
    @Operation(summary = "系统设置-组织-项目-禁用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#id)", msClass = OrganizationProjectLogService.class)
    public void disable(@PathVariable String id) {
        organizationProjectService.disable(id, SessionUtils.getCurrentUserName());
    }
}
