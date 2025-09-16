package cn.master.hub.controller;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.log.annotation.Log;
import cn.master.hub.service.ProjectUserRoleService;
import cn.master.hub.service.log.ProjectUserRoleLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/15
 */
@Tag(name = "项目管理-项目与权限-用户组")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/role/project")
public class ProjectUserRoleController {
    private final ProjectUserRoleService projectUserRoleService;

    @GetMapping("/permission/setting/{id}")
    @Operation(summary = "项目管理-项目与权限-用户组-获取用户组对应的权限配置")
    @Parameter(name = "id", description = "用户组ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    // @RequiresPermissions(PermissionConstants.PROJECT_GROUP_READ)
    public List<PermissionDefinitionItem> getPermissionSetting(@PathVariable String id) {
        return projectUserRoleService.getPermissionSetting(id);
    }
    @PostMapping("/permission/update")
    @Operation(summary = "项目管理-项目与权限-用户组-修改用户组对应的权限配置")
//    @RequiresPermissions(PermissionConstants.PROJECT_GROUP_UPDATE)
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updatePermissionSettingLog(#request)", msClass = ProjectUserRoleLogService.class)
//    @CheckProjectOwner(resourceId = "#request.getUserRoleId()", resourceType = "user_role", resourceCol = "scope_id")
    public void updatePermissionSetting(@Validated @RequestBody PermissionSettingUpdateRequest request) {
        projectUserRoleService.updatePermissionSetting(request);
    }
}
