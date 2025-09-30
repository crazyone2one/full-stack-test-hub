package cn.master.hub.service;

import cn.master.hub.dto.BaseTreeNode;
import cn.master.hub.dto.api.ModuleUpdateRequest;
import cn.master.hub.dto.request.ApiModuleRequest;
import cn.master.hub.dto.request.ModuleCreateRequest;
import cn.master.hub.entity.ApiDefinitionModule;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 接口模块 服务层。
 *
 * @author 11's papa
 * @since 2025-09-29
 */
public interface ApiDefinitionModuleService extends IService<ApiDefinitionModule> {
    String add(ModuleCreateRequest request, String operator);

    List<BaseTreeNode> getTree(ApiModuleRequest request, boolean deleted, boolean containRequest);

    void update(ModuleUpdateRequest request, String operator);

    void deleteModule(String id, String operator);

    void deleteModule(List<String> deleteIds, String operator, String projectId);

    Map<String, Long> moduleCount(ApiModuleRequest request, boolean b);
}
