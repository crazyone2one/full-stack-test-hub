package cn.master.hub.service.impl;

import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.request.BasePageRequest;
import cn.master.hub.dto.system.TaskHubScheduleDTO;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.entity.SystemSchedule;
import cn.master.hub.service.BaseTaskHubService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.SystemOrganizationTableDef.SYSTEM_ORGANIZATION;
import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.hub.entity.table.SystemScheduleTableDef.SYSTEM_SCHEDULE;

/**
 * @author Created by 11's papa on 2025/9/25
 */
@Service
public class BaseTaskHubServiceImpl implements BaseTaskHubService {
    @Override
    public Page<TaskHubScheduleDTO> getSchedulePage(BasePageRequest request, List<String> projectIds) {
        QueryChain<SystemSchedule> scheduleQueryChain = QueryChain.of(SystemSchedule.class)
                .select(SYSTEM_SCHEDULE.ALL_COLUMNS)
                .select(" QRTZ_TRIGGERS.NEXT_FIRE_TIME AS next_time")
                .select(" QRTZ_TRIGGERS.PREV_FIRE_TIME AS last_time")
                .select(SYSTEM_PROJECT.ORGANIZATION_ID,SYSTEM_PROJECT.NUM.as("resourceNum"),SYSTEM_PROJECT.NAME.as("projectName"))
                .from(SYSTEM_SCHEDULE).as("schedule")
                .leftJoin(SYSTEM_PROJECT).on(SYSTEM_SCHEDULE.PROJECT_ID.eq(SYSTEM_PROJECT.ID))
                .leftJoin("QRTZ_TRIGGERS").on("schedule.job = QRTZ_TRIGGERS.JOB_GROUP and schedule.key =QRTZ_TRIGGERS.TRIGGER_NAME")
                .where(SYSTEM_SCHEDULE.PROJECT_ID.in(projectIds))
                .and(SYSTEM_SCHEDULE.NAME.like(request.getKeyword())
                        .or(SYSTEM_SCHEDULE.NUM.like(request.getKeyword())));
        Page<TaskHubScheduleDTO> page = scheduleQueryChain.pageAs(new Page<>(request.getPage(), request.getPageSize()), TaskHubScheduleDTO.class);
        processTaskCenterSchedule(page.getRecords(), projectIds);
        return page;
    }

    private void processTaskCenterSchedule(List<TaskHubScheduleDTO> list, List<String> projectIds) {
        if (CollectionUtils.isNotEmpty(list)) {
            if (CollectionUtils.isEmpty(projectIds)) {
                projectIds = list.stream().map(TaskHubScheduleDTO::getProjectId).collect(Collectors.toList());
            }
            List<OptionDTO> orgListByProjectList = QueryChain.of(SystemProject.class)
                    .select(SYSTEM_PROJECT.ID.as("value"), SYSTEM_ORGANIZATION.NAME.as("label"))
                    .from(SYSTEM_PROJECT).innerJoin(SYSTEM_ORGANIZATION).on(SYSTEM_PROJECT.ORGANIZATION_ID.eq(SYSTEM_ORGANIZATION.ID))
                    .where(SYSTEM_PROJECT.ID.in(projectIds))
                    .listAs(OptionDTO.class);
            Map<String, String> orgMap = orgListByProjectList.stream().collect(Collectors.toMap(OptionDTO::getValue, OptionDTO::getLabel));
            for (TaskHubScheduleDTO item : list) {
                item.setOrganizationName(orgMap.getOrDefault(item.getProjectId(), StringUtils.EMPTY));
            }
        }
    }
}
