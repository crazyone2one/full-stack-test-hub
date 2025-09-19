package cn.master.hub.controller;

import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.system.OrgUserExtend;
import cn.master.hub.dto.system.OrganizationProjectRequest;
import cn.master.hub.dto.system.request.OrgMemberExtendProjectRequest;
import cn.master.hub.dto.system.request.OrganizationMemberExtendRequest;
import cn.master.hub.dto.system.request.OrganizationMemberUpdateRequest;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.service.SystemOrganizationService;
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
 * @author Created by 11's papa on 2025/9/18
 */
@Tag(name = "系统设置-组织-成员")
@RestController
@RequiredArgsConstructor
@RequestMapping("/organization")
public class OrganizationController {
    private final SystemOrganizationService organizationService;

    @PostMapping("/member/list")
    @Operation(summary = "系统设置-组织-成员-获取组织成员列表")
    public Page<OrgUserExtend> getMemberList(@Validated @RequestBody OrganizationProjectRequest organizationRequest) {
        return organizationService.getMemberPageByOrg(organizationRequest);
    }

    @PostMapping("/role/update-member")
    @Operation(summary = "系统设置-组织-成员-添加组织成员至用户组")
    public void addMemberRole(@Validated @RequestBody OrganizationMemberExtendRequest organizationMemberExtendRequest) {
        organizationService.addMemberRole(organizationMemberExtendRequest, SessionUtils.getCurrentUserName());
    }

    @PostMapping("/update-member")
    @Operation(summary = "系统设置-组织-成员-更新用户")
    public void updateMember(@Validated @RequestBody OrganizationMemberUpdateRequest organizationMemberExtendRequest) {
        organizationService.updateMember(organizationMemberExtendRequest, SessionUtils.getCurrentUserName(), "/organization/update-member", OperationLogModule.SETTING_ORGANIZATION_MEMBER);
    }

    @PostMapping("/project/add-member")
    @Operation(summary = "系统设置-组织-成员-添加组织成员至项目")
    public void addMemberToProject(@Validated @RequestBody OrgMemberExtendProjectRequest orgMemberExtendProjectRequest) {
        organizationService.addMemberToProject(orgMemberExtendProjectRequest, SessionUtils.getCurrentUserName());
    }

    @GetMapping("/remove-member/{organizationId}/{userId}")
    @Operation(summary = "系统设置-组织-成员-删除组织成员")
    @Parameters({
            @Parameter(name = "organizationId", description = "组织ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED)),
            @Parameter(name = "userId", description = "成员ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    })
    public void removeMember(@PathVariable String organizationId, @PathVariable String userId) {
        organizationService.removeMember(organizationId, userId, SessionUtils.getCurrentUserName());
    }

    @GetMapping("/user/role/list/{organizationId}")
    @Operation(summary = "系统设置-组织-成员-获取当前组织下的所有自定义用户组以及组织级别的用户组")
    public List<OptionDTO> getUserRoleList(@PathVariable(value = "organizationId") String organizationId) {
        return organizationService.getUserRoleList(organizationId);
    }
}
