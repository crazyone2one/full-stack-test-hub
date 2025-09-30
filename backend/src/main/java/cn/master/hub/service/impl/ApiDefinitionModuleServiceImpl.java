package cn.master.hub.service.impl;

import cn.master.hub.constants.ModuleConstants;
import cn.master.hub.dto.BaseTreeNode;
import cn.master.hub.dto.api.ModuleUpdateRequest;
import cn.master.hub.dto.request.ApiModuleRequest;
import cn.master.hub.dto.request.ModuleCreateRequest;
import cn.master.hub.entity.ApiDefinitionModule;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.mapper.ApiDefinitionModuleMapper;
import cn.master.hub.service.ApiDefinitionModuleService;
import cn.master.hub.service.ModuleTreeService;
import cn.master.hub.service.log.ApiDefinitionModuleLogService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cn.master.hub.entity.table.ApiDefinitionModuleTableDef.API_DEFINITION_MODULE;
import static cn.master.hub.service.ModuleTreeService.LIMIT_POS;

/**
 * 接口模块 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-29
 */
@Service
@RequiredArgsConstructor
public class ApiDefinitionModuleServiceImpl extends ServiceImpl<ApiDefinitionModuleMapper, ApiDefinitionModule> implements ApiDefinitionModuleService {
    private final ApiDefinitionModuleLogService apiDefinitionModuleLogService;
    private final ModuleTreeService moduleTreeService;
    private static final String UNPLANNED_API = "api_unplanned_request";
    private static final String MODULE_NO_EXIST = "api_module.not.exist";
    private static final String DEBUG_MODULE_COUNT_ALL = "all";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(ModuleCreateRequest request, String operator) {
        ApiDefinitionModule module = new ApiDefinitionModule();
        module.setName(request.getName());
        module.setParentId(request.getParentId());
        module.setProjectId(request.getProjectId());
        module.setCreateUser(operator);
        checkDataValidity(module);
        module.setPos(getNextOrder(request.getParentId()));
        module.setUpdateUser(operator);
        mapper.insert(module);
        apiDefinitionModuleLogService.saveAddLog(module, operator);
        return module.getId();
    }

    @Override
    public List<BaseTreeNode> getTree(ApiModuleRequest request, boolean deleted, boolean containRequest) {
        List<BaseTreeNode> fileModuleList = selectBaseByRequest(request);
        List<BaseTreeNode> baseTreeNodes = moduleTreeService.buildTreeAndCountResource(fileModuleList, true, Translator.get(UNPLANNED_API));
        if (!containRequest || CollectionUtils.isEmpty(request.getProtocols())) {
            return baseTreeNodes;
        }
        return List.of();
//        List<ApiTreeNode> apiTreeNodeList = extApiDefinitionModuleMapper.selectApiDataByRequest(request, deleted);
//        return apiDebugModuleService.getBaseTreeNodes(apiTreeNodeList, baseTreeNodes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ModuleUpdateRequest request, String operator) {
        ApiDefinitionModule module = checkModuleExist(request.getId());
        ApiDefinitionModule updateModule = new ApiDefinitionModule();
        updateModule.setId(request.getId());
        updateModule.setName(request.getName());
        updateModule.setParentId(module.getParentId());
        updateModule.setProjectId(module.getProjectId());
        this.checkDataValidity(updateModule);
        mapper.update(updateModule);
        //记录日志
        apiDefinitionModuleLogService.saveUpdateLog(updateModule, operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModule(String id, String operator) {
        ApiDefinitionModule deleteModule = checkModuleExist(id);
        deleteModule(List.of(id), operator, deleteModule.getProjectId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModule(List<String> deleteIds, String operator, String projectId) {
        if (CollectionUtils.isEmpty(deleteIds)) {
            return;
        }
        QueryChain<ApiDefinitionModule> moduleQueryChain = queryChain().select(API_DEFINITION_MODULE.ID, API_DEFINITION_MODULE.NAME, API_DEFINITION_MODULE.PARENT_ID)
                .select(API_DEFINITION_MODULE.POS, API_DEFINITION_MODULE.PROJECT_ID)
                .select("'MODULE' AS type").from(API_DEFINITION_MODULE)
                .where(API_DEFINITION_MODULE.ID.in(deleteIds))
                .orderBy(API_DEFINITION_MODULE.POS.desc());
        List<BaseTreeNode> baseTreeNodes = mapper.selectListByQueryAs(moduleQueryChain, BaseTreeNode.class);
        mapper.deleteBatchByIds(deleteIds);
        //记录日志
        apiDefinitionModuleLogService.saveDeleteModuleLog(baseTreeNodes, operator, projectId);

        batchDeleteData(deleteIds, operator, projectId);

        List<String> childrenIds = queryChain().select(API_DEFINITION_MODULE.ID).where(API_DEFINITION_MODULE.PARENT_ID.in(deleteIds)).listAs(String.class);
        if (CollectionUtils.isNotEmpty(childrenIds)) {
            deleteModule(childrenIds, operator, projectId);
        }
    }

    @Override
    public Map<String, Long> moduleCount(ApiModuleRequest request, boolean b) {
        return Map.of();
    }

    private void batchDeleteData(List<String> deleteIds, String operator, String projectId) {
        // todo
    }

    private ApiDefinitionModule checkModuleExist(String moduleId) {
        return queryChain().where(API_DEFINITION_MODULE.ID.eq(moduleId))
                .oneOpt().orElseThrow(() -> new CustomException(Translator.get(MODULE_NO_EXIST)));
    }

    private List<BaseTreeNode> selectBaseByRequest(ApiModuleRequest request) {
        return queryChain()
                .select(API_DEFINITION_MODULE.ID, API_DEFINITION_MODULE.NAME, API_DEFINITION_MODULE.PARENT_ID)
                .select(API_DEFINITION_MODULE.POS, API_DEFINITION_MODULE.PROJECT_ID)
                .select("'MODULE' AS type").from(API_DEFINITION_MODULE)
                .where(API_DEFINITION_MODULE.PROJECT_ID.eq(request.getProjectId()))
                .and(API_DEFINITION_MODULE.NAME.like(request.getKeyword()))
                .and(API_DEFINITION_MODULE.ID.in(request.getModuleIds()))
                .orderBy(API_DEFINITION_MODULE.POS.desc())
                .listAs(BaseTreeNode.class);
    }

    private void checkDataValidity(ApiDefinitionModule module) {
        if (!Strings.CS.equals(module.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
            boolean exists = queryChain().where(API_DEFINITION_MODULE.ID.eq(module.getParentId())
                    .and(API_DEFINITION_MODULE.PROJECT_ID.eq(module.getProjectId()))).exists();
            if (!exists) {
                throw new CustomException(Translator.get("parent.node.not_blank"));
            }
        }
        boolean exists = queryChain().where(API_DEFINITION_MODULE.PARENT_ID.eq(module.getParentId())
                .and(API_DEFINITION_MODULE.PROJECT_ID.eq(module.getProjectId()))
                .and(API_DEFINITION_MODULE.NAME.eq(module.getName()))
                .and(API_DEFINITION_MODULE.ID.ne(module.getId()))).exists();
        if (exists) {
            throw new CustomException(Translator.get("node.name.repeat"));
        }
    }

    private Long getNextOrder(String parentId) {
        Long maxPos = queryChain().select(QueryMethods.max(API_DEFINITION_MODULE.POS)).where(API_DEFINITION_MODULE.PARENT_ID.eq(parentId)).oneAs(Long.class);
        if (maxPos == null) {
            return LIMIT_POS;
        } else {
            return maxPos + LIMIT_POS;
        }
    }
}
