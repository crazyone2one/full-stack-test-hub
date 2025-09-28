package cn.master.hub.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Transactional(rollbackFor = Exception.class)
public abstract class TestPlanResourceService extends TestPlanSortService{
    public abstract void deleteBatchByTestPlanId(List<String> testPlanIdList);
}
