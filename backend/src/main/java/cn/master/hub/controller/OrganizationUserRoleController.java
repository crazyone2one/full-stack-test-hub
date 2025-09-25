package cn.master.hub.controller;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.dto.system.request.OrganizationUserRoleMemberEditRequest;
import cn.master.hub.dto.system.request.OrganizationUserRoleMemberRequest;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.log.annotation.Log;
import cn.master.hub.service.OrganizationUserRoleService;
import cn.master.hub.service.log.OrganizationUserRoleLogService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/15
 */
@Tag(name = "系统设置-组织-用户组")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/role/organization")
public class OrganizationUserRoleController {
    private final OrganizationUserRoleService organizationUserRoleService;

    @GetMapping("/list/{organizationId}")
    @Operation(summary = "系统设置-组织-用户组-获取用户组列表")
    @Parameter(name = "organizationId", description = "当前组织ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    // @RequiresPermissions(PermissionConstants.ORGANIZATION_USER_ROLE_READ)
    public List<UserRole> list(@PathVariable String organizationId) {
        return organizationUserRoleService.list(organizationId);
    }

    @GetMapping("/permission/setting/{id}")
    @Operation(summary = "系统设置-组织-用户组-获取用户组对应的权限配置")
    @Parameter(name = "id", description = "用户组ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    // @RequiresPermissions(PermissionConstants.ORGANIZATION_USER_ROLE_READ)
    public List<PermissionDefinitionItem> getPermissionSetting(@PathVariable String id) {
        return organizationUserRoleService.getPermissionSetting(id);
    }

    @PostMapping("/permission/update")
    @Operation(summary = "系统设置-组织-用户组-修改用户组对应的权限配置")
    //@RequiresPermissions(PermissionConstants.ORGANIZATION_USER_ROLE_READ_UPDATE)
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updatePermissionSettingLog(#request)", msClass = OrganizationUserRoleLogService.class)
    //@CheckOrgOwner(resourceId = "#request.getUserRoleId()", resourceType = "user_role", resourceCol = "scope_id")
    public void updatePermissionSetting(@Validated @RequestBody PermissionSettingUpdateRequest request) {
        organizationUserRoleService.updatePermissionSetting(request);
    }

    @PostMapping("/list-member")
    @Operation(summary = "系统设置-组织-用户组-获取成员列表")
    public Page<SystemUser> listMember(@Validated @RequestBody OrganizationUserRoleMemberRequest request) {
        return organizationUserRoleService.listMember(request);
    }

    @PostMapping("/add-member")
    @Operation(summary = "系统设置-组织-用户组-添加用户组成员")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.editMemberLog(#request)", msClass = OrganizationUserRoleLogService.class)
    public void addMember(@Validated @RequestBody OrganizationUserRoleMemberEditRequest request) {
        organizationUserRoleService.addMember(request, SessionUtils.getCurrentUserName());
    }

    @PostMapping("/remove-member")
    @Operation(summary = "系统设置-组织-用户组-删除用户组成员")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.editMemberLog(#request)", msClass = OrganizationUserRoleLogService.class)
    public void removeMember(@Validated @RequestBody OrganizationUserRoleMemberEditRequest request) {
        organizationUserRoleService.removeMember(request);
    }
    @GetMapping("/get-member/option/{organizationId}/{roleId}")
    @Operation(summary = "系统设置-组织-用户组-获取成员下拉选项")
    @Parameters({
            @Parameter(name = "organizationId", description = "组织ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED)),
            @Parameter(name = "roleId", description = "用户组ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    })
    public List<UserExtendDTO> getMember(@PathVariable String organizationId,
                                         @PathVariable String roleId,
                                         @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                         @RequestParam(required = false) String keyword) {
        return organizationUserRoleService.getMember(organizationId, roleId, keyword);
    }
}
