package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.TestPlanReport;
import cn.master.hub.mapper.TestPlanReportMapper;
import cn.master.hub.service.TestPlanReportService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试计划报告 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
@Service
public class TestPlanReportServiceImpl extends ServiceImpl<TestPlanReportMapper, TestPlanReport>  implements TestPlanReportService{

    @Override
    public void deleteByTestPlanIds(List<String> testPlanIds) {
        // todo 待实现
    }
}
