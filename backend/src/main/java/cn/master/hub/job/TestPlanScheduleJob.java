package cn.master.hub.job;

import cn.master.hub.handler.schedule.BaseScheduleJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Slf4j
public class TestPlanScheduleJob extends BaseScheduleJob {
    @Override
    protected void businessExecute(JobExecutionContext context) {
        log.info("开始执行测试计划的定时任务. ID：{}", resourceId);
    }
    public static JobKey getJobKey(String testPlanId) {
        return new JobKey(testPlanId, TestPlanScheduleJob.class.getName());
    }

    public static TriggerKey getTriggerKey(String testPlanId) {
        return new TriggerKey(testPlanId, TestPlanScheduleJob.class.getName());
    }
}
