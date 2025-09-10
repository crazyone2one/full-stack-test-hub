package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.TestResourcePool;
import cn.master.hub.mapper.TestResourcePoolMapper;
import cn.master.hub.service.TestResourcePoolService;
import org.springframework.stereotype.Service;

/**
 * 测试资源池 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-10
 */
@Service
public class TestResourcePoolServiceImpl extends ServiceImpl<TestResourcePoolMapper, TestResourcePool>  implements TestResourcePoolService{

}
