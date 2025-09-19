package cn.master.hub.controller;

import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.project.ProjectUserDTO;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.dto.system.request.ProjectMemberRequest;
import cn.master.hub.service.ProjectMemberService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/19
 */
@Tag(name = "项目管理-成员")
@RestController
@RequiredArgsConstructor
@RequestMapping("/project/member")
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    @PostMapping("/list")
    @Operation(summary = "项目管理-成员-列表查询")
    public Page<ProjectUserDTO> listMember(@Validated @RequestBody ProjectMemberRequest request) {
        return projectMemberService.pageMember(request);
    }

    @GetMapping("/get-member/option/{projectId}")
    @Operation(summary = "项目管理-成员-获取成员下拉选项")
    public List<UserExtendDTO> getMemberOption(@PathVariable String projectId,
                                               @Schema(description = "查询关键字，根据邮箱和用户名查询")
                                               @RequestParam(value = "keyword", required = false) String keyword) {
        return projectMemberService.getMemberOption(projectId, keyword);
    }

    @GetMapping("/get-role/option/{projectId}")
    @Operation(summary = "项目管理-成员-获取用户组下拉选项")
    public List<OptionDTO> getRoleOption(@PathVariable String projectId) {
        return projectMemberService.getRoleOption(projectId);
    }
}
