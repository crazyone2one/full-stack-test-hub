package cn.master.hub.controller;

import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.dto.system.UserRoleUpdateRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.log.annotation.Log;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import cn.master.hub.service.GlobalUserRoleService;
import cn.master.hub.service.log.GlobalUserRoleLogService;
import cn.master.hub.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/12
 */
@Tag(name = "系统设置-系统-用户组")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/role/global")
public class GlobalUserRoleController {
    private final GlobalUserRoleService globalUserRoleService;

    @PostMapping("/add")
    @Operation(summary = "系统设置-系统-用户组-添加自定义全局用户组")
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = GlobalUserRoleLogService.class)
    public UserRole add(@Validated({Created.class}) @RequestBody UserRoleUpdateRequest request) {
        UserRole userRole = new UserRole();
        userRole.setCreateUser(SessionUtils.getCurrentUserName());
        BeanUtils.copyProperties(request, userRole);
        return globalUserRoleService.add(userRole);
    }

    @PostMapping("/permission/update")
    @Operation(summary = "系统设置-系统-用户组-编辑全局用户组对应的权限配置")
    // @RequiresPermissions(PermissionConstants.SYSTEM_USER_ROLE_UPDATE)
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#request)", msClass = GlobalUserRoleLogService.class)
    public void updatePermissionSetting(@Validated @RequestBody PermissionSettingUpdateRequest request) {
        globalUserRoleService.updatePermissionSetting(request);
    }

    @GetMapping("/list")
    @Operation(summary = "系统设置-系统-用户组-获取全局用户组列表")
//    @RequiresPermissions(PermissionConstants.SYSTEM_USER_ROLE_READ)
    public List<UserRole> list() {
        return globalUserRoleService.list();
    }

    @GetMapping("/permission/setting/{id}")
    @Operation(summary = "系统设置-系统-用户组-获取全局用户组对应的权限配置")
//    @RequiresPermissions(PermissionConstants.SYSTEM_USER_ROLE_READ)
    public List<PermissionDefinitionItem> getPermissionSetting(@PathVariable String id) {
        return globalUserRoleService.getPermissionSetting(id);
    }

    @PostMapping("/update")
    @Operation(summary = "系统设置-系统-用户组-更新自定义全局用户组")
    // @RequiresPermissions(PermissionConstants.SYSTEM_USER_ROLE_UPDATE)
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#request)", msClass = GlobalUserRoleLogService.class)
    public UserRole update(@Validated({Updated.class}) @RequestBody UserRoleUpdateRequest request) {
        UserRole userRole = new UserRole();
        BeanUtils.copyProperties(request, userRole);
        return globalUserRoleService.update(userRole);
    }

    @GetMapping("/delete/{id}")
    @Operation(summary = "系统设置-系统-用户组-删除自定义全局用户组")
    // @RequiresPermissions(PermissionConstants.SYSTEM_USER_ROLE_DELETE)
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#id)", msClass = GlobalUserRoleLogService.class)
    public void delete(@PathVariable String id) {
        globalUserRoleService.delete(id, SessionUtils.getCurrentUserName());
    }
}
