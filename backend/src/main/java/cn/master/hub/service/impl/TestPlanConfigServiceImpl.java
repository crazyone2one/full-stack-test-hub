package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.TestPlanConfig;
import cn.master.hub.mapper.TestPlanConfigMapper;
import cn.master.hub.service.TestPlanConfigService;
import org.springframework.stereotype.Service;

/**
 * 测试计划配置 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
@Service
public class TestPlanConfigServiceImpl extends ServiceImpl<TestPlanConfigMapper, TestPlanConfig>  implements TestPlanConfigService{

}
