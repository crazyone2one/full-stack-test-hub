package cn.master.hub.controller;

import cn.master.hub.dto.request.BasePageRequest;
import cn.master.hub.dto.system.OrganizationProjectOptionsDTO;
import cn.master.hub.dto.system.TaskHubScheduleDTO;
import cn.master.hub.dto.system.request.BaseScheduleConfigRequest;
import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.entity.SystemSchedule;
import cn.master.hub.service.BaseTaskHubService;
import cn.master.hub.service.SystemProjectService;
import cn.master.hub.service.SystemScheduleService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/25
 */
@Tag(name = "系统任务中心")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/task-center")
public class SystemTaskController {
    private final SystemProjectService systemProjectService;
    private final BaseTaskHubService baseTaskHubService;
    private final SystemScheduleService systemScheduleService;

    @PostMapping("/schedule/save")
    @Operation(summary = "系统-任务中心-后台执行任务列表")
    public void saveSchedule(@Validated @RequestBody SystemSchedule request) {
        systemScheduleService.addSchedule(request);
    }

    @PostMapping("/schedule/page")
    @Operation(summary = "系统-任务中心-后台执行任务列表")
    public Page<TaskHubScheduleDTO> scheduleList(@Validated @RequestBody BasePageRequest request) {
        return baseTaskHubService.getSchedulePage(request, null);
    }

    @PostMapping("/schedule-config")
    @Operation(summary = "系统-任务中心-定时任务配置")
    public String scheduleList(@Validated @RequestBody BaseScheduleConfigRequest request) {
        return systemScheduleService.scheduleConfig(request, SessionUtils.getCurrentUserName());
    }

    @GetMapping("/project/options")
    @Operation(summary = "系统-任务中心-获取全部项目下拉选项")
    public List<OrganizationProjectOptionsDTO> getAllProject() {
        return systemProjectService.getProjectOptions(null);
    }

    @GetMapping("/organization/options")
    @Operation(summary = "系统-任务中心-获取全部组织下拉选项")
    public List<OrganizationProjectOptionsDTO> getAllOrganization() {
        return QueryChain.of(SystemOrganization.class).listAs(OrganizationProjectOptionsDTO.class);
    }
}
