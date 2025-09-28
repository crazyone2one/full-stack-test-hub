package cn.master.hub.service;

import cn.master.hub.dto.system.request.BaseScheduleConfigRequest;
import cn.master.hub.dto.system.request.ScheduleConfig;
import cn.master.hub.entity.SystemSchedule;
import com.mybatisflex.core.service.IService;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * 定时任务 服务层。
 *
 * @author 11's papa
 * @since 2025-09-25
 */
public interface SystemScheduleService extends IService<SystemSchedule> {
    void addSchedule(SystemSchedule schedule);

    SystemSchedule getScheduleByResource(String resourceId, String job);

    void addOrUpdateCronJob(SystemSchedule request, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz);

    String scheduleConfig(ScheduleConfig scheduleConfig, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz, String operator);

    String scheduleConfig(BaseScheduleConfigRequest request, String operator);
}
