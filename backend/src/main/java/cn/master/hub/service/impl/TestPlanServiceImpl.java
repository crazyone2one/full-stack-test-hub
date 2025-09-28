package cn.master.hub.service.impl;

import cn.master.hub.constants.TestPlanConstants;
import cn.master.hub.dto.request.TestPlanBatchProcessRequest;
import cn.master.hub.entity.TestPlan;
import cn.master.hub.entity.TestPlanConfig;
import cn.master.hub.entity.table.TestPlanTableDef;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.job.TestPlanScheduleJob;
import cn.master.hub.mapper.TestPlanConfigMapper;
import cn.master.hub.service.SystemScheduleService;
import cn.master.hub.service.TestPlanReportService;
import cn.master.hub.service.TestPlanResourceService;
import cn.master.hub.service.TestPlanService;
import cn.master.hub.service.log.TestPlanLogService;
import cn.master.hub.util.BatchProcessUtils;
import cn.master.hub.util.CommonBeanFactory;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.master.hub.entity.table.TestPlanConfigTableDef.TEST_PLAN_CONFIG;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Service("testPlanService")
public class TestPlanServiceImpl extends BaseTestPlanServiceImpl implements TestPlanService {
    private final TestPlanConfigMapper testPlanConfigMapper;
    private final SystemScheduleService scheduleService;
    private final TestPlanLogService testPlanLogService;

    public TestPlanServiceImpl(TestPlanConfigMapper testPlanConfigMapper,
                               SystemScheduleService scheduleService,
                               TestPlanLogService testPlanLogService) {
        this.testPlanConfigMapper = testPlanConfigMapper;
        this.scheduleService = scheduleService;
        this.testPlanLogService = testPlanLogService;
    }

    @Override
    public void batchDelete(TestPlanBatchProcessRequest request, String operator, String requestUrl, String requestMethod) {
        List<String> deleteIdList = request.getSelectIds();
        if (CollectionUtils.isNotEmpty(deleteIdList)) {
            List<TestPlan> deleteTestPlanList = mapper.selectListByIds(deleteIdList);
            if (CollectionUtils.isNotEmpty(deleteTestPlanList)) {
                List<String> testPlanGroupList = new ArrayList<>();
                List<String> testPlanIdList = new ArrayList<>();
                for (TestPlan testPlan : deleteTestPlanList) {
                    if (Strings.CS.equals(testPlan.getType(), TestPlanConstants.TEST_PLAN_TYPE_GROUP)) {
                        testPlanGroupList.add(testPlan.getId());
                    } else {
                        testPlanIdList.add(testPlan.getId());
                    }
                }
//                testPlanSendNoticeService.batchSendNotice(request.getProjectId(), deleteIdList, userMapper.selectByPrimaryKey(operator), NoticeConstants.Event.DELETE);
                deleteByList(testPlanIdList);
                // 计划组的删除暂时预留
                deleteGroupByList(testPlanGroupList);
                //记录日志
                testPlanLogService.saveBatchLog(deleteTestPlanList, operator, requestUrl, requestMethod, OperationLogType.DELETE.name());
            }
        }
    }

    private void deleteGroupByList(List<String> testPlanGroupIds) {
        if (CollectionUtils.isNotEmpty(testPlanGroupIds)) {
            TestPlanReportService testPlanReportService = CommonBeanFactory.getBean(TestPlanReportService.class);
            assert testPlanReportService != null;
            BatchProcessUtils.consumerByString(testPlanGroupIds, (deleteGroupIds) -> {
                /*
                 * 计划组删除逻辑{第一版需求: 删除组, 组下的子计划Group置为None}:
                 * 1. 查询计划组下的全部子计划并删除(级联删除这些子计划的关联资源)
                 */
                List<TestPlan> deleteGroupPlans = QueryChain.of(TestPlan.class).where(TestPlanTableDef.TEST_PLAN.GROUP_ID.in(deleteGroupIds)).list();
                List<String> deleteGroupPlanIds = deleteGroupPlans.stream().map(TestPlan::getId).toList();
                List<String> allDeleteIds = ListUtils.union(deleteGroupIds, deleteGroupPlanIds);
                if (CollectionUtils.isNotEmpty(allDeleteIds)) {
                    // 级联删除子计划关联的资源(计划组不存在关联的资源,但是存在报告)
                    cascadeDeleteTestPlanIds(allDeleteIds, testPlanReportService);
                    mapper.deleteBatchByIds(allDeleteIds);
                }
            });
        }
    }

    private void deleteByList(List<String> testPlanIds) {
        if (CollectionUtils.isNotEmpty(testPlanIds)) {
            TestPlanReportService testPlanReportService = CommonBeanFactory.getBean(TestPlanReportService.class);

            BatchProcessUtils.consumerByString(testPlanIds, (deleteIds) -> {
                mapper.deleteBatchByIds(deleteIds);
                //级联删除
                assert testPlanReportService != null;
                cascadeDeleteTestPlanIds(deleteIds, testPlanReportService);
            });
        }
    }

    private void cascadeDeleteTestPlanIds(List<String> testPlanIds, TestPlanReportService testPlanReportService) {
        //删除当前计划对应的资源
        Map<String, TestPlanResourceService> subTypes = CommonBeanFactory.getBeansOfType(TestPlanResourceService.class);
        subTypes.forEach((k, t) -> t.deleteBatchByTestPlanId(testPlanIds));
        //删除测试计划配置
        testPlanConfigMapper.deleteByQuery(QueryChain.of(TestPlanConfig.class).where(TEST_PLAN_CONFIG.TEST_PLAN_ID.in(testPlanIds)));
        //删除测试计划报告
        testPlanReportService.deleteByTestPlanIds(testPlanIds);
        //删除定时任务
        scheduleService.deleteByResourceIds(testPlanIds, TestPlanScheduleJob.class.getName());
    }
}
