package cn.master.hub.service;

import cn.master.hub.dto.request.TestPlanBatchProcessRequest;

/**
 * @author Created by 11's papa on 2025/9/28
 */
public interface TestPlanService extends BaseTestPlanService{
    void batchDelete(TestPlanBatchProcessRequest request, String operator, String requestUrl, String requestMethod);
}
