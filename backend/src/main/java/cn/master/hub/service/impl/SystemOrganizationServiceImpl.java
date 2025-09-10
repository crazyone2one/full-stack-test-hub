package cn.master.hub.service.impl;

import cn.master.hub.dto.OptionDTO;
import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.mapper.SystemOrganizationMapper;
import cn.master.hub.service.SystemOrganizationService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 组织 服务层实现。
 *
 * @author the2n
 * @since 2025-09-01
 */
@Service
public class SystemOrganizationServiceImpl extends ServiceImpl<SystemOrganizationMapper, SystemOrganization> implements SystemOrganizationService {

    @Override
    public List<OptionDTO> listAll() {
        List<SystemOrganization> organizations = mapper.selectAll();
        return organizations.stream().map(o -> new OptionDTO(o.getId(), o.getName())).toList();
    }
}
