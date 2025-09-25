package cn.master.hub.service;

import cn.master.hub.dto.system.TableBatchProcessResponse;
import cn.master.hub.dto.system.UserExcludeOptionDTO;
import cn.master.hub.dto.system.UserRoleRelationUserDTO;
import cn.master.hub.dto.system.request.GlobalUserRoleRelationQueryRequest;
import cn.master.hub.dto.system.request.GlobalUserRoleRelationUpdateRequest;
import cn.master.hub.dto.system.request.UserRoleBatchRelationRequest;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import com.mybatisflex.core.paginate.Page;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/23
 */
public interface GlobalUserRoleRelationService extends BaseUserRoleRelationService {
    TableBatchProcessResponse batchAdd(@Validated({Created.class, Updated.class}) UserRoleBatchRelationRequest request, String operator);

    void add(GlobalUserRoleRelationUpdateRequest request);

    Page<UserRoleRelationUserDTO> userPageByUG(GlobalUserRoleRelationQueryRequest request);

    void delete(String id);

    List<UserExcludeOptionDTO> getExcludeSelectOption(String roleId, String keyword);
}
