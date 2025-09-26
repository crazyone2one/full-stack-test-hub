package cn.master.hub.service.impl;

import cn.master.hub.dto.system.TestResourceDTO;
import cn.master.hub.dto.system.TestResourcePoolDTO;
import cn.master.hub.dto.system.request.QueryResourcePoolRequest;
import cn.master.hub.entity.TestResourcePool;
import cn.master.hub.mapper.TestResourcePoolMapper;
import cn.master.hub.service.TestResourcePoolService;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试资源池 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-10
 */
@Service
public class TestResourcePoolServiceImpl extends ServiceImpl<TestResourcePoolMapper, TestResourcePool>  implements TestResourcePoolService{

    @Override
    public Page<TestResourcePoolDTO> pageResourcePools(QueryResourcePoolRequest request) {
        List<TestResourcePool> testResourcePools = queryChain().list();
        List<TestResourcePoolDTO> testResourcePoolDTOS = new ArrayList<>();
        testResourcePools.forEach(pool -> {
            byte[] configuration = pool.getConfiguration();
            String testResourceDTOStr = new String(configuration);
            TestResourceDTO testResourceDTO = JacksonUtils.parseObject(testResourceDTOStr, TestResourceDTO.class);
            TestResourcePoolDTO testResourcePoolDTO = new TestResourcePoolDTO();
            BeanUtils.copyProperties(pool, testResourcePoolDTO);
            testResourcePoolDTO.setTestResourceDTO(testResourceDTO);
        });
        return null;
    }
}
