package cn.master.hub.controller;

import cn.master.hub.entity.SystemProject;
import cn.master.hub.service.SystemProjectService;
import cn.master.hub.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/8
 */
@RestController
@Tag(name = "项目管理")
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    private final SystemProjectService projectService;

    @GetMapping("/list/options/{organizationId}")
    @Operation(summary = "根据组织ID获取所有有权限的项目")
    public List<SystemProject> getUserProject(@PathVariable String organizationId) {
        return projectService.getUserProject(organizationId, SessionUtils.getCurrentUserName());
    }
}
