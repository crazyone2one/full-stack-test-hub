package cn.master.hub.service;

import cn.master.hub.dto.OptionDTO;
import com.mybatisflex.core.service.IService;
import cn.master.hub.entity.SystemOrganization;

import java.util.List;

/**
 * 组织 服务层。
 *
 * @author the2n
 * @since 2025-09-01
 */
public interface SystemOrganizationService extends IService<SystemOrganization> {

    List<OptionDTO> listAll();
}
