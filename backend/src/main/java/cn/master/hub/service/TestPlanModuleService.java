package cn.master.hub.service;

import cn.master.hub.dto.BaseTreeNode;
import cn.master.hub.dto.request.NodeMoveRequest;
import cn.master.hub.dto.request.TestPlanModuleCreateRequest;
import cn.master.hub.dto.request.TestPlanModuleUpdateRequest;
import cn.master.hub.entity.TestPlanModule;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 测试计划模块 服务层。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
public interface TestPlanModuleService extends IService<TestPlanModule> {
    List<BaseTreeNode> getTree(String projectId);

    String add(TestPlanModuleCreateRequest request, String operator, String requestUrl, String requestMethod);

    void update(TestPlanModuleUpdateRequest request, String userId, String requestUrl, String requestMethod);

    void deleteModule(String deleteId, String operator, String requestUrl, String requestMethod);

    void deleteModule(List<String> deleteIds, String projectId, String operator, String requestUrl, String requestMethod);

    void moveNode(NodeMoveRequest request, String currentUser, String requestUrl, String requestMethod);
}
