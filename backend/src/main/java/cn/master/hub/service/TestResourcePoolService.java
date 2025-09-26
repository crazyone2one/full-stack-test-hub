package cn.master.hub.service;

import cn.master.hub.dto.system.TestResourcePoolDTO;
import cn.master.hub.dto.system.request.QueryResourcePoolRequest;
import cn.master.hub.entity.TestResourcePool;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 测试资源池 服务层。
 *
 * @author 11's papa
 * @since 2025-09-10
 */
public interface TestResourcePoolService extends IService<TestResourcePool> {
    Page<TestResourcePoolDTO> pageResourcePools(QueryResourcePoolRequest request);
}
