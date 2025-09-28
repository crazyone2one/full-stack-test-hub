package cn.master.hub.service.impl;

import cn.master.hub.entity.TestPlan;
import cn.master.hub.mapper.TestPlanMapper;
import cn.master.hub.service.BaseTestPlanService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 测试计划 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
@Primary
@Service("BaseTestPlanService")
public class BaseTestPlanServiceImpl extends ServiceImpl<TestPlanMapper, TestPlan>  implements BaseTestPlanService {

}
