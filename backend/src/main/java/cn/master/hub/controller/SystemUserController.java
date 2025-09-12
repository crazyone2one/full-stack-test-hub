package cn.master.hub.controller;

import cn.master.hub.constants.UserSource;
import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.dto.request.BasePageRequest;
import cn.master.hub.dto.request.UserEditRequest;
import cn.master.hub.dto.response.UserTableResponse;
import cn.master.hub.dto.system.UserSelectOption;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import cn.master.hub.service.SystemUserService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author the2n
 * @since 2025-08-29
 */
@RestController
@Tag(name = "用户接口")
@RequiredArgsConstructor
@RequestMapping("/system/user")
public class SystemUserController {

    private final SystemUserService systemUserService;

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
}
