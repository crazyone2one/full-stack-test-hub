package cn.master.hub.controller;

import cn.master.hub.dto.system.UserExcludeOptionDTO;
import cn.master.hub.dto.system.UserRoleRelationUserDTO;
import cn.master.hub.dto.system.request.GlobalUserRoleRelationQueryRequest;
import cn.master.hub.dto.system.request.GlobalUserRoleRelationUpdateRequest;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.log.annotation.Log;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.service.GlobalUserRoleRelationService;
import cn.master.hub.service.log.GlobalUserRoleRelationLogService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/24
 */
@RestController
@Tag(name = "系统设置-系统-用户组-用户关联关系")
@RequestMapping("/user/role/relation/global")
public class GlobalUserRoleRelationController {
    private final GlobalUserRoleRelationService globalUserRoleRelationService;

    public GlobalUserRoleRelationController(@Qualifier("globalUserRoleRelationService") GlobalUserRoleRelationService globalUserRoleRelationService) {
        this.globalUserRoleRelationService = globalUserRoleRelationService;
    }
    @PostMapping("/add")
    @Operation(summary = "系统设置-系统-用户组-用户关联关系-创建全局用户组和用户的关联关系")
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = GlobalUserRoleRelationLogService.class)
    public void add(@Validated({Created.class}) @RequestBody GlobalUserRoleRelationUpdateRequest request) {
        request.setCreateUser(SessionUtils.getCurrentUserName());
        globalUserRoleRelationService.add(request);
    }
    @GetMapping("/delete/{id}")
    @Operation(summary = "系统设置-系统-用户组-用户关联关系-删除全局用户组和用户的关联关系")
    @Log(type = OperationLogType.DELETE, expression = "#msClass.deleteLog(#id)", msClass = GlobalUserRoleRelationLogService.class)
    public void delete(@PathVariable String id) {
        globalUserRoleRelationService.delete(id);
    }

    @PostMapping("/list")
    @Operation(summary = "系统设置-系统-用户组-用户关联关系-获取全局用户组对应的用户列表")
    public Page<UserRoleRelationUserDTO> list(@Validated @RequestBody GlobalUserRoleRelationQueryRequest request) {
        return globalUserRoleRelationService.userPageByUG(request);
    }
    @GetMapping("/user/option/{roleId}")
    @Operation(summary = "系统设置-系统-用户组-用户关联关系-获取需要关联的用户选项")
    public List<UserExcludeOptionDTO> getSelectOption(@Schema(description = "用户组ID", requiredMode = Schema.RequiredMode.REQUIRED)
                                                      @PathVariable String roleId,
                                                      @Schema(description = "查询关键字，根据邮箱和用户名查询", requiredMode = Schema.RequiredMode.REQUIRED)
                                                      @RequestParam(value = "keyword", required = false) String keyword) {
        return globalUserRoleRelationService.getExcludeSelectOption(roleId, keyword);
    }
}
