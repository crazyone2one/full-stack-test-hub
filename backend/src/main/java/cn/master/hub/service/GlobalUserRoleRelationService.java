package cn.master.hub.service;

import cn.master.hub.dto.system.TableBatchProcessResponse;
import cn.master.hub.dto.system.request.GlobalUserRoleRelationUpdateRequest;
import cn.master.hub.dto.system.request.UserRoleBatchRelationRequest;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import org.springframework.validation.annotation.Validated;

/**
 * @author Created by 11's papa on 2025/9/23
 */
public interface GlobalUserRoleRelationService extends BaseUserRoleRelationService {
    TableBatchProcessResponse batchAdd(@Validated({Created.class, Updated.class}) UserRoleBatchRelationRequest request, String operator);

    void add(GlobalUserRoleRelationUpdateRequest request);
}
