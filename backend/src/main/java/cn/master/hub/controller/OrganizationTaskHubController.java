package cn.master.hub.controller;

import cn.master.hub.dto.system.OrganizationProjectOptionsDTO;
import cn.master.hub.service.SystemProjectService;
import cn.master.hub.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/25
 */
@Tag(name = "组织任务中心")
@RestController
@RequiredArgsConstructor
@RequestMapping("/organization/task-center")
public class OrganizationTaskHubController {
    private final SystemProjectService systemProjectService;

    @GetMapping("/project/options")
    @Operation(summary = "系统-任务中心-获取组织下全部项目下拉选项")
    public List<OrganizationProjectOptionsDTO> getOrgProject() {
        return systemProjectService.getProjectOptions(SessionUtils.getCurrentOrganizationId());
    }
}
