package cn.master.hub.controller;

import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.request.OrganizationEditRequest;
import cn.master.hub.dto.system.OrganizationDTO;
import cn.master.hub.dto.system.OrganizationProjectRequest;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.dto.system.request.MemberRequest;
import cn.master.hub.dto.system.request.OrganizationMemberRequest;
import cn.master.hub.dto.system.request.OrganizationMemberUpdateRequest;
import cn.master.hub.entity.SystemOrganization;
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
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 组织 控制层。
 *
 * @author the2n
 * @since 2025-09-01
 */
@RestController
@Tag(name = "组织接口")
@RequestMapping("/system/organization")
@RequiredArgsConstructor
public class SystemOrganizationController {

    private final SystemOrganizationService systemOrganizationService;

    /**
     * 保存组织。
     *
     * @param systemOrganization 组织
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description = "保存组织")
    public boolean save(@RequestBody @Parameter(description = "组织") SystemOrganization systemOrganization) {
        return systemOrganizationService.save(systemOrganization);
    }

    /**
     * 根据主键删除组织。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除组织")
    public boolean remove(@PathVariable @Parameter(description = "组织主键") String id) {
        return systemOrganizationService.removeById(id);
    }

    /**
     * 根据主键更新组织。
     *
     * @param request 组织
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新组织")
    public void update(@RequestBody @Parameter(description = "组织主键") OrganizationEditRequest request) {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        BeanUtils.copyProperties(request, organizationDTO);
        organizationDTO.setUpdateUser(SessionUtils.getCurrentUserName());
        systemOrganizationService.update(organizationDTO);
    }

    /**
     * 查询所有组织。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有组织")
    public List<SystemOrganization> list() {
        return systemOrganizationService.list();
    }

    @PostMapping("/list-member")
    @Operation(summary = "系统设置-系统-组织与项目-组织-获取组织成员列表")
    public Page<UserExtendDTO> listMember(@Validated @RequestBody OrganizationProjectRequest request) {
        return systemOrganizationService.getMemberPageBySystem(request);
    }

    @PostMapping("/add-member")
    @Operation(summary = "系统设置-系统-组织与项目-组织-添加组织成员")
    public void addMember(@Validated @RequestBody OrganizationMemberRequest request) {
        systemOrganizationService.addMemberBySystem(request, SessionUtils.getCurrentUserName());
    }

    @GetMapping("/remove-member/{organizationId}/{userId}")
    @Operation(summary = "系统设置-系统-组织与项目-组织-删除组织成员")
    @Parameters({
            @Parameter(name = "organizationId", description = "组织ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED)),
            @Parameter(name = "userId", description = "成员ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    })
    public void removeMember(@PathVariable String organizationId, @PathVariable String userId) {
        systemOrganizationService.removeMember(organizationId, userId, SessionUtils.getCurrentUserName());
    }

    /**
     * 根据主键获取组织。
     *
     * @param id 组织主键
     * @return 组织详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取组织")
    public SystemOrganization getInfo(@PathVariable @Parameter(description = "组织主键") String id) {
        return systemOrganizationService.getById(id);
    }

    /**
     * 分页查询组织。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询组织")
    public Page<SystemOrganization> page(@Parameter(description = "分页信息") Page<SystemOrganization> page) {
        return systemOrganizationService.page(page);
    }

    @PostMapping("/option/all")
    @Operation(summary = "系统设置-系统-组织与项目-组织-获取系统所有组织下拉选项")
    public List<OptionDTO> listAll() {
        return systemOrganizationService.listAll();
    }

    @GetMapping("/total")
    @Operation(summary = "系统设置-系统-组织与项目-组织-获取组织和项目总数")
    public Map<String, Long> getTotal(@RequestParam(value = "organizationId", required = false) String organizationId) {
        return systemOrganizationService.getTotal(organizationId);
    }

    @GetMapping("/get-option/{sourceId}")
    @Operation(summary = "系统设置-系统-组织与项目-获取成员下拉选项")
    @Parameter(name = "sourceId", description = "组织ID或项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    public List<UserExtendDTO> getMemberOption(@PathVariable String sourceId,
                                               @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                               @RequestParam(value = "keyword", required = false) String keyword) {
        return systemOrganizationService.getMemberOption(sourceId, keyword);
    }

    @PostMapping("/member-list")
    @Operation(summary = "系统设置-系统-组织与项目-获取添加成员列表")
    public Page<UserExtendDTO> getMemberOptionList(@Validated @RequestBody MemberRequest request) {
        return systemOrganizationService.getMemberList(request);
    }

    @PostMapping("/update-member")
    @Operation(summary = "系统设置-系统-组织与项目-组织-成员-更新成员用户组")
    public void updateMember(@Validated @RequestBody OrganizationMemberUpdateRequest organizationMemberExtendRequest) {
        systemOrganizationService.updateMember(organizationMemberExtendRequest, SessionUtils.getCurrentUserName(), "/system/organization/update-member", OperationLogModule.SETTING_SYSTEM_ORGANIZATION);
    }
}
