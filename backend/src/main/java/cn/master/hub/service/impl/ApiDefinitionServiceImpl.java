package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.ApiDefinition;
import cn.master.hub.mapper.ApiDefinitionMapper;
import cn.master.hub.service.ApiDefinitionService;
import org.springframework.stereotype.Service;

/**
 * 接口定义 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-29
 */
@Service
public class ApiDefinitionServiceImpl extends ServiceImpl<ApiDefinitionMapper, ApiDefinition>  implements ApiDefinitionService{

}
