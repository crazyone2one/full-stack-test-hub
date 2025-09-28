package cn.master.hub.service;

import com.mybatisflex.core.service.IService;
import cn.master.hub.entity.TestPlanReport;

import java.util.List;

/**
 * 测试计划报告 服务层。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
public interface TestPlanReportService extends IService<TestPlanReport> {

    void deleteByTestPlanIds(List<String> testPlanIds);
}
