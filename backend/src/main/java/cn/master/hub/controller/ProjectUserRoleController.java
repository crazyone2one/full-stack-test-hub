package cn.master.hub.controller;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.project.ProjectUserRoleDTO;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.dto.request.ProjectUserRoleEditRequest;
import cn.master.hub.dto.request.project.ProjectUserRoleMemberEditRequest;
import cn.master.hub.dto.request.project.ProjectUserRoleMemberRequest;
import cn.master.hub.dto.request.project.ProjectUserRoleRequest;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.log.annotation.Log;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import cn.master.hub.service.ProjectUserRoleService;
import cn.master.hub.service.log.ProjectUserRoleLogService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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

    @PostMapping("/add")
    @Operation(summary = "项目管理-项目与权限-用户组-添加用户组")
    // @RequiresPermissions(PermissionConstants.PROJECT_GROUP_ADD)
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = ProjectUserRoleLogService.class)
    public UserRole add(@Validated({Created.class}) @RequestBody ProjectUserRoleEditRequest request) {
        UserRole userRole = new UserRole();
        userRole.setCreateUser(SessionUtils.getCurrentUserName());
        BeanUtils.copyProperties(request, userRole);
        return projectUserRoleService.add(userRole);
    }

    @PostMapping("/update")
    @Operation(summary = "项目管理-项目与权限-用户组-修改用户组")
    //@RequiresPermissions(PermissionConstants.PROJECT_GROUP_UPDATE)
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#request)", msClass = ProjectUserRoleLogService.class)
    //@CheckProjectOwner(resourceId = "#request.getId()", resourceType = "user_role", resourceCol = "scope_id")
    public UserRole update(@Validated({Updated.class}) @RequestBody ProjectUserRoleEditRequest request) {
        UserRole userRole = new UserRole();
        BeanUtils.copyProperties(request, userRole);
        return projectUserRoleService.update(userRole);
    }

    @PostMapping("/permission/update")
    @Operation(summary = "项目管理-项目与权限-用户组-修改用户组对应的权限配置")
//    @RequiresPermissions(PermissionConstants.PROJECT_GROUP_UPDATE)
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updatePermissionSettingLog(#request)", msClass = ProjectUserRoleLogService.class)
//    @CheckProjectOwner(resourceId = "#request.getUserRoleId()", resourceType = "user_role", resourceCol = "scope_id")
    public void updatePermissionSetting(@Validated @RequestBody PermissionSettingUpdateRequest request) {
        projectUserRoleService.updatePermissionSetting(request);
    }

    @PostMapping("/page")
    @Operation(summary = "项目管理-项目与权限-用户组-获取用户组列表")
    public Page<ProjectUserRoleDTO> page(@Validated @RequestBody ProjectUserRoleRequest request) {
        return projectUserRoleService.page(request);
    }

    @PostMapping("/list-member")
    @Operation(summary = "项目管理-项目与权限-用户组-获取成员列表")
    public Page<SystemUser> page(@Validated @RequestBody ProjectUserRoleMemberRequest request) {
        return projectUserRoleService.listMember(request);
    }

    @PostMapping("/add-member")
    @Operation(summary = "项目管理-项目与权限-用户组-添加用户组成员")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.editMemberLog(#request)", msClass = ProjectUserRoleLogService.class)
    public void addMember(@Validated @RequestBody ProjectUserRoleMemberEditRequest request) {
        projectUserRoleService.addMember(request, SessionUtils.getCurrentUserName());
    }

    @GetMapping("/delete/{id}")
    @Operation(summary = "项目管理-项目与权限-用户组-删除用户组")
    //@RequiresPermissions(PermissionConstants.PROJECT_GROUP_DELETE)
    @Parameter(name = "id", description = "用户组ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#id)", msClass = ProjectUserRoleLogService.class)
    // @CheckProjectOwner(resourceId = "#id", resourceType = "user_role", resourceCol = "scope_id")
    public void delete(@PathVariable String id) {
        projectUserRoleService.delete(id, SessionUtils.getCurrentUserName());
    }

    @GetMapping("/get-member/option/{projectId}/{roleId}")
    @Operation(summary = "项目管理-项目与权限-用户组-获取成员下拉选项")
    //@RequiresPermissions(value = {PermissionConstants.PROJECT_GROUP_READ})
    @Parameters({
            @Parameter(name = "projectId", description = "当前项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED)),
            @Parameter(name = "roleId", description = "用户组ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    })
    public List<UserExtendDTO> getMember(@PathVariable String projectId,
                                         @PathVariable String roleId,
                                         @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                         @RequestParam(required = false) String keyword) {
        return projectUserRoleService.getMember(projectId, roleId, keyword);
    }

    @PostMapping("/remove-member")
    @Operation(summary = "项目管理-项目与权限-用户组-删除用户组成员")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.editMemberLog(#request)", msClass = ProjectUserRoleLogService.class)
    public void removeMember(@Validated @RequestBody ProjectUserRoleMemberEditRequest request) {
        projectUserRoleService.removeMember(request);
    }
}
