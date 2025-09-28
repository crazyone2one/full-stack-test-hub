package cn.master.hub.service.impl;

import cn.master.hub.constants.ApplicationNumScope;
import cn.master.hub.constants.ScheduleResourceType;
import cn.master.hub.constants.ScheduleType;
import cn.master.hub.dto.system.request.BaseScheduleConfigRequest;
import cn.master.hub.dto.system.request.ScheduleConfig;
import cn.master.hub.entity.SystemSchedule;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.schedule.ScheduleManager;
import cn.master.hub.handler.uid.NumGenerator;
import cn.master.hub.mapper.SystemScheduleMapper;
import cn.master.hub.service.SystemScheduleService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.hub.entity.table.SystemScheduleTableDef.SYSTEM_SCHEDULE;

/**
 * 定时任务 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-25
 */
@Service
public class SystemScheduleServiceImpl extends ServiceImpl<SystemScheduleMapper, SystemSchedule> implements SystemScheduleService {
    private final ScheduleManager scheduleManager;

    public SystemScheduleServiceImpl(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }

    @Override
    public void addSchedule(SystemSchedule schedule) {
        schedule.setResourceType(ScheduleResourceType.LOAD_TEST.name());
        schedule.setType(ScheduleType.CRON.name());
        schedule.setNum(getNextNum(schedule.getProjectId()));
        schedule.setCreateUser(SessionUtils.getCurrentUserName());
        mapper.insert(schedule);
    }

    @Override
    public SystemSchedule getScheduleByResource(String resourceId, String job) {
        return queryChain().where(SystemSchedule::getResourceId).eq(resourceId)
                .and(SystemSchedule::getJob).eq(job).one();
    }

    @Override
    public void addOrUpdateCronJob(SystemSchedule request, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz) {
        Boolean enable = request.getEnable();
        String cronExpression = request.getValue();
        if (BooleanUtils.isTrue(enable) && StringUtils.isNotBlank(cronExpression)) {
            try {
                scheduleManager.addOrUpdateCronJob(jobKey, triggerKey, clazz, cronExpression,
                        scheduleManager.getDefaultJobDataMap(request, cronExpression, request.getCreateUser()));
            } catch (SchedulerException e) {
                throw new CustomException("定时任务开启异常: " + e.getMessage());
            }
        } else {
            try {
                scheduleManager.removeJob(jobKey, triggerKey);
            } catch (Exception e) {
                throw new CustomException("定时任务关闭异常: " + e.getMessage());
            }
        }
    }

    @Override
    public String scheduleConfig(ScheduleConfig scheduleConfig, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz, String operator) {
        SystemSchedule schedule;
        QueryChain<SystemSchedule> scheduleQueryChain = queryChain().where(SYSTEM_SCHEDULE.RESOURCE_ID.eq(scheduleConfig.getResourceId())
                .and(SYSTEM_SCHEDULE.JOB.eq(clazz.getName())));
        List<SystemSchedule> scheduleList = scheduleQueryChain.list();
        if (CollectionUtils.isNotEmpty(scheduleList)) {
            schedule = scheduleConfig.genCronSchedule(scheduleList.getFirst());
            schedule.setJob(clazz.getName());
            mapper.updateByQuery(schedule, scheduleQueryChain);
        } else {
            schedule = scheduleConfig.genCronSchedule(null);
            schedule.setJob(clazz.getName());
            schedule.setCreateUser(operator);
            schedule.setNum(getNextNum(scheduleConfig.getProjectId()));
            mapper.insert(schedule);
        }
        JobDataMap jobDataMap = scheduleManager.getDefaultJobDataMap(schedule, scheduleConfig.getCron(), schedule.getCreateUser());
        /*
        scheduleManager.modifyCronJobTime方法如同它的方法名所说，只能修改定时任务的触发时间。
        如果定时任务的配置数据jobData发生了变化，上面方法是无法更新配置数据的。
        所以，如果配置数据发生了变化，做法就是先删除运行中的定时任务，再重新添加定时任务。

        以上的更新逻辑配合 enable 开关，可以简化为下列写法：
         */
        scheduleManager.removeJob(jobKey, triggerKey);
        if (BooleanUtils.isTrue(schedule.getEnable())) {
            scheduleManager.addCronJob(jobKey, triggerKey, clazz, scheduleConfig.getCron(), jobDataMap);
        }
        return schedule.getId();
    }

    @Override
    public String scheduleConfig(BaseScheduleConfigRequest request, String operator) {
        return "";
    }

    private Long getNextNum(String projectId) {
        return NumGenerator.nextNum(projectId, ApplicationNumScope.TASK);
    }
}
