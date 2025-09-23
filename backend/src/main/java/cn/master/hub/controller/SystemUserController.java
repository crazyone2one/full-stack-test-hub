package cn.master.hub.controller;

import cn.master.hub.constants.UserSource;
import cn.master.hub.dto.BaseTreeNode;
import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.dto.request.BasePageRequest;
import cn.master.hub.dto.request.UserBatchCreateRequest;
import cn.master.hub.dto.request.UserEditRequest;
import cn.master.hub.dto.response.UserBatchCreateResponse;
import cn.master.hub.dto.response.UserTableResponse;
import cn.master.hub.dto.system.ProjectAddMemberBatchRequest;
import cn.master.hub.dto.system.TableBatchProcessDTO;
import cn.master.hub.dto.system.TableBatchProcessResponse;
import cn.master.hub.dto.system.UserSelectOption;
import cn.master.hub.dto.system.request.UserChangeEnableRequest;
import cn.master.hub.dto.system.request.UserRoleBatchRelationRequest;
import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.log.annotation.Log;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import cn.master.hub.service.GlobalUserRoleRelationService;
import cn.master.hub.service.SystemOrganizationService;
import cn.master.hub.service.SystemProjectService;
import cn.master.hub.service.SystemUserService;
import cn.master.hub.service.log.UserLogService;
import cn.master.hub.util.SessionUtils;
import cn.master.hub.util.TreeNodeParseUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户 控制层。
 *
 * @author the2n
 * @since 2025-08-29
 */
@RestController
@Tag(name = "用户接口")
@RequestMapping("/system/user")
public class SystemUserController {

    private final SystemUserService systemUserService;
    private final UserLogService userLogService;
    private final GlobalUserRoleRelationService globalUserRoleRelationService;
    private final SystemProjectService systemProjectService;
    private final SystemOrganizationService organizationService;

    public SystemUserController(SystemUserService systemUserService, UserLogService userLogService,
                                @Qualifier("globalUserRoleRelationService") GlobalUserRoleRelationService globalUserRoleRelationService,
                                SystemProjectService systemProjectService, SystemOrganizationService organizationService) {
        this.systemUserService = systemUserService;
        this.userLogService = userLogService;
        this.globalUserRoleRelationService = globalUserRoleRelationService;
        this.systemProjectService = systemProjectService;
        this.organizationService = organizationService;
    }

    /**
     * 保存用户。
     *
     * @param userCreateDTO 用户
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description = "保存用户")
    public String save(@Validated({Created.class}) @RequestBody UserCreateInfo userCreateDTO) {
        return systemUserService.addUser(userCreateDTO, UserSource.LOCAL.name(), SessionUtils.getCurrentUserName());
    }

    @PostMapping("/add")
    @Operation(summary = "系统设置-系统-用户-添加用户")
    public UserBatchCreateResponse addUser(@Validated({Created.class}) @RequestBody UserBatchCreateRequest userCreateDTO) {
        return systemUserService.addUser(userCreateDTO, UserSource.LOCAL.name(), SessionUtils.getCurrentUserName());
    }

    /**
     * 根据主键删除用户。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除用户")
    public boolean remove(@PathVariable @Parameter(description = "用户主键") String id) {
        return systemUserService.removeById(id);
    }

    @PostMapping("/delete")
    @Operation(summary = "系统设置-系统-用户-删除用户")
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#request)", msClass = UserLogService.class)
    public TableBatchProcessResponse deleteUser(@Validated @RequestBody TableBatchProcessDTO request) {
        return systemUserService.deleteUser(request, SessionUtils.getCurrentUserName(), SessionUtils.getCurrentUser().getUsername());
    }

    /**
     * 根据主键更新用户。
     *
     * @param request 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "系统设置-系统-用户-修改用户")
    public UserEditRequest update(@Validated({Updated.class}) @RequestBody UserEditRequest request) {
        return systemUserService.updateUser(request, SessionUtils.getCurrentUserName());
    }

    @PostMapping("/update/enable")
    @Operation(summary = "系统设置-系统-用户-启用/禁用用户")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.batchUpdateEnableLog(#request)", msClass = UserLogService.class)
    public TableBatchProcessResponse updateUserEnable(@Validated @RequestBody UserChangeEnableRequest request) {
        return systemUserService.updateUserEnable(request, SessionUtils.getCurrentUserName(), SessionUtils.getCurrentUser().getUsername());
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有用户")
    public List<SystemUser> list() {
        return systemUserService.list();
    }

    /**
     * 根据主键获取用户。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取用户")
    public SystemUser getInfo(@PathVariable @Parameter(description = "用户主键") String id) {
        return systemUserService.getById(id);
    }

    /**
     * 分页查询用户。
     *
     * @param request 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    @Operation(description = "分页查询用户")
    public Page<UserTableResponse> page(@Validated @RequestBody BasePageRequest request) {
        return systemUserService.getUserPage(request);
    }

    @GetMapping("/get/global/system/role")
    @Operation(summary = "系统设置-系统-用户-查找系统级用户组")
    public List<UserSelectOption> getGlobalSystemRole() {
        return systemUserService.getGlobalSystemRoleList();
    }

    @GetMapping("/get/organization")
    @Operation(summary = "系统设置-系统-用户-用户批量操作-查找组织")
    public List<OptionDTO> getOrganization() {
        return organizationService.listAll();
    }

    @GetMapping("/get/project")
    @Operation(summary = "系统设置-系统-用户-用户批量操作-查找项目")
    public List<BaseTreeNode> getProject() {
        Map<SystemOrganization, List<SystemProject>> orgProjectMap = organizationService.getOrgProjectMap();
        return TreeNodeParseUtils.parseOrgProjectMap(orgProjectMap);
    }

    @PostMapping("/add-org-member")
    @Operation(summary = "系统设置-系统-用户-批量添加用户到组织")
    public TableBatchProcessResponse addMember(@Validated @RequestBody UserRoleBatchRelationRequest userRoleBatchRelationRequest) {
        return systemUserService.addMemberToOrg(userRoleBatchRelationRequest);
    }

    @PostMapping("/add/batch/user-role")
    @Operation(summary = "系统设置-系统-用户-批量添加用户到多个用户组中")
    public TableBatchProcessResponse batchAddUserGroupRole(@Validated({Created.class}) @RequestBody UserRoleBatchRelationRequest request) {
        TableBatchProcessResponse returnResponse = globalUserRoleRelationService.batchAdd(request, SessionUtils.getCurrentUserName());
        userLogService.batchAddUserRoleLog(request, SessionUtils.getCurrentUserName());
        return returnResponse;
    }

    @PostMapping("/add-project-member")
    @Operation(summary = "系统设置-系统-用户-批量添加用户到项目")
    public TableBatchProcessResponse addProjectMember(@Validated @RequestBody UserRoleBatchRelationRequest userRoleBatchRelationRequest) {
        ProjectAddMemberBatchRequest request = new ProjectAddMemberBatchRequest();
        request.setProjectIds(userRoleBatchRelationRequest.getRoleIds());
        request.setUserIds(userRoleBatchRelationRequest.getSelectIds());
        systemProjectService.addProjectMember(request, SessionUtils.getCurrentUserName());
        userLogService.batchAddProjectLog(userRoleBatchRelationRequest, SessionUtils.getCurrentUserName());
        return new TableBatchProcessResponse(userRoleBatchRelationRequest.getSelectIds().size(), userRoleBatchRelationRequest.getSelectIds().size());
    }
    @PostMapping("/reset/password")
    @Operation(summary = "系统设置-系统-用户-重置用户密码")
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.resetPasswordLog(#request)", msClass = UserLogService.class)
    public TableBatchProcessResponse resetPassword(@Validated @RequestBody TableBatchProcessDTO request) {
        return systemUserService.resetPassword(request, SessionUtils.getCurrentUserName());
    }

}
