package cn.master.hub.service.impl;

import cn.master.hub.constants.ModuleConstants;
import cn.master.hub.dto.*;
import cn.master.hub.dto.project.NodeSortUtils;
import cn.master.hub.dto.request.NodeMoveRequest;
import cn.master.hub.dto.request.TestPlanBatchProcessRequest;
import cn.master.hub.dto.request.TestPlanModuleCreateRequest;
import cn.master.hub.dto.request.TestPlanModuleUpdateRequest;
import cn.master.hub.entity.TestPlan;
import cn.master.hub.entity.TestPlanModule;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.mapper.TestPlanModuleMapper;
import cn.master.hub.service.ModuleTreeService;
import cn.master.hub.service.TestPlanModuleService;
import cn.master.hub.service.TestPlanService;
import cn.master.hub.service.log.TestPlanModuleLogService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static cn.master.hub.entity.table.TestPlanModuleTableDef.TEST_PLAN_MODULE;
import static cn.master.hub.entity.table.TestPlanTableDef.TEST_PLAN;

/**
 * 测试计划模块 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
@Service
public class TestPlanModuleServiceImpl extends ServiceImpl<TestPlanModuleMapper, TestPlanModule> implements TestPlanModuleService {
    protected static final long LIMIT_POS = NodeSortUtils.DEFAULT_NODE_INTERVAL_POS;
    private final ModuleTreeService moduleTreeService;
    private final TestPlanModuleLogService testPlanModuleLogService;
    private final TestPlanService testPlanService;

    public TestPlanModuleServiceImpl(ModuleTreeService moduleTreeService,
                                     TestPlanModuleLogService testPlanModuleLogService,
                                     @Qualifier("testPlanService") TestPlanService testPlanService) {
        this.moduleTreeService = moduleTreeService;
        this.testPlanModuleLogService = testPlanModuleLogService;
        this.testPlanService = testPlanService;
    }

    @Override
    public List<BaseTreeNode> getTree(String projectId) {
        List<BaseTreeNode> fileModuleList = mapper.selectListWithRelationsByQueryAs(
                queryChain().where(TestPlanModule::getProjectId).eq(projectId).orderBy(TestPlanModule::getPos).desc(),
                BaseTreeNode.class);
        return moduleTreeService.buildTreeAndCountResource(fileModuleList, true, Translator.get("unplanned.plan"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(TestPlanModuleCreateRequest request, String operator, String requestUrl, String requestMethod) {
        TestPlanModule testPlanModule = new TestPlanModule();
        testPlanModule.setName(request.getName());
        testPlanModule.setParentId(request.getParentId());
        testPlanModule.setProjectId(request.getProjectId());
        checkDataValidity(testPlanModule);
        testPlanModule.setPos(this.countPos(request.getParentId()));
        testPlanModule.setCreateUser(operator);
        testPlanModule.setUpdateUser(operator);
        mapper.insert(testPlanModule);
        //记录日志
        testPlanModuleLogService.saveAddLog(testPlanModule, operator, requestUrl, requestMethod);
        return testPlanModule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TestPlanModuleUpdateRequest request, String userId, String requestUrl, String requestMethod) {
        TestPlanModule module = mapper.selectOneById(request.getId());
        TestPlanModule updateModule = new TestPlanModule();
        updateModule.setId(request.getId());
        updateModule.setName(request.getName().trim());
        updateModule.setParentId(module.getParentId());
        updateModule.setProjectId(module.getProjectId());
        this.checkDataValidity(updateModule);
        updateModule.setUpdateUser(userId);
        mapper.update(updateModule);
        TestPlanModule newModule = mapper.selectOneById(request.getId());
        testPlanModuleLogService.saveUpdateLog(module, newModule, module.getProjectId(), userId, requestUrl, requestMethod);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModule(String deleteId, String operator, String requestUrl, String requestMethod) {
        TestPlanModule deleteModule = mapper.selectOneById(deleteId);
        if (deleteModule != null) {
            this.deleteModule(Collections.singletonList(deleteId), deleteModule.getProjectId(), operator, requestUrl, requestMethod);
            //记录日志
            testPlanModuleLogService.saveDeleteLog(deleteModule, operator, requestUrl, requestMethod);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModule(List<String> deleteIds, String projectId, String operator, String requestUrl, String requestMethod) {
        if (CollectionUtils.isEmpty(deleteIds)) {
            return;
        }
        List<String> planDeleteIds = QueryChain.of(TestPlan.class)
                .select(TEST_PLAN.ID).from(TEST_PLAN)
                .where(TEST_PLAN.MODULE_ID.in(deleteIds)).listAs(String.class);
        if (CollectionUtils.isNotEmpty(planDeleteIds)) {
            TestPlanBatchProcessRequest request = new TestPlanBatchProcessRequest();
            request.setModuleIds(deleteIds);
            request.setSelectAll(false);
            request.setProjectId(projectId);
            request.setSelectIds(planDeleteIds);
            testPlanService.batchDelete(request, operator, requestUrl, requestMethod);
        }
        List<String> childrenIds = queryChain()
                .select(TEST_PLAN_MODULE.ID)
                .where(TEST_PLAN_MODULE.PARENT_ID.in(deleteIds)).listAs(String.class);
        if (CollectionUtils.isNotEmpty(childrenIds)) {
            deleteModule(childrenIds, projectId, operator, requestUrl, requestMethod);
        }
        mapper.deleteBatchByIds(deleteIds);
    }

    @Override
    public void moveNode(NodeMoveRequest request, String currentUser, String requestUrl, String requestMethod) {
        NodeSortDTO nodeSortDTO = moduleTreeService.getNodeSortDTO(request, this::selectBaseModuleById, this::selectModuleByParentIdAndPosOperator);
        // 拖拽后, 父级模块下存在同名模块
        long count = queryChain().where(TEST_PLAN_MODULE.PARENT_ID.eq(nodeSortDTO.getParent().getId())
                .and(TEST_PLAN_MODULE.NAME.eq(nodeSortDTO.getNode().getName()))
                .and(TEST_PLAN_MODULE.PROJECT_ID.eq(nodeSortDTO.getNode().getProjectId()))
                .and(TEST_PLAN_MODULE.ID.ne(nodeSortDTO.getNode().getId()))
        ).count();
        if (count > 0) {
            throw new CustomException(Translator.get("test_plan_module_already_exists"));
        }
        //节点换到了别的节点下,要先更新parent节点再计算sort
        if (queryChain().where(TEST_PLAN_MODULE.PARENT_ID.eq(nodeSortDTO.getParent().getId())
                .and(TEST_PLAN_MODULE.ID.eq(request.getDragNodeId()))).count() == 0) {
            updateChain().set(TEST_PLAN_MODULE.PARENT_ID, nodeSortDTO.getParent().getId())
                    .where(TEST_PLAN_MODULE.ID.eq(request.getDragNodeId())).update();
        }
        ModuleSortCountResultDTO sort = moduleTreeService.sort(nodeSortDTO);
        updateChain().set(TEST_PLAN_MODULE.POS, sort.getPos()).where(TEST_PLAN_MODULE.ID.eq(nodeSortDTO.getNode().getId())).update();
        if (sort.isRefreshPos()) {
            refreshPos(nodeSortDTO.getParent().getId());
        }
        //记录日志
        testPlanModuleLogService.saveMoveLog(nodeSortDTO, currentUser, requestUrl, requestMethod);
    }

    private void refreshPos(String parentId) {
        List<String> childrenIdSortByPos = queryChain()
                .select(TEST_PLAN_MODULE.ID)
                .where(TEST_PLAN_MODULE.PARENT_ID.eq(parentId)).listAs(String.class);
        for (int i = 0; i < childrenIdSortByPos.size(); i++) {
            String nodeId = childrenIdSortByPos.get(i);
            updateChain().set(TEST_PLAN_MODULE.POS, (i + 1) * LIMIT_POS).where(TEST_PLAN_MODULE.ID.eq(nodeId)).update();
        }
    }

    private BaseModule selectBaseModuleById(String id) {
        return queryChain().where(TEST_PLAN_MODULE.ID.eq(id)).oneAs(BaseModule.class);
    }

    private BaseModule selectModuleByParentIdAndPosOperator(NodeSortQueryParam sortParam) {
        return queryChain().where(TEST_PLAN_MODULE.PARENT_ID.eq(sortParam.getParentId())
                        .and(TEST_PLAN_MODULE.POS.ge(sortParam.getPos()).when("moreThan".equals(sortParam.getOperator())))
                        .and(TEST_PLAN_MODULE.POS.le(sortParam.getPos()).when("lessThan".equals(sortParam.getOperator())))
                ).orderBy(TEST_PLAN_MODULE.POS.desc())
                .limit(1)
                .oneAs(BaseModule.class);
    }

    private void checkDataValidity(TestPlanModule module) {
        if (!Strings.CS.equals(module.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
            if (!queryChain().where(TestPlanModule::getId).eq(module.getParentId()).exists()) {
                throw new CustomException(Translator.get("parent.node.not_blank"));
            }
            if (StringUtils.isNotBlank(module.getProjectId())) {
                boolean exists = queryChain().where(TEST_PLAN_MODULE.ID.eq(module.getParentId())
                        .and(TEST_PLAN_MODULE.PROJECT_ID.eq(module.getProjectId()))).exists();
                if (!exists) {
                    throw new CustomException(Translator.get("project.cannot.match.parent"));
                }
            }
        }
        boolean exists = queryChain().where(TEST_PLAN_MODULE.ID.ne(module.getId())
                .and(TEST_PLAN_MODULE.PROJECT_ID.eq(module.getProjectId()))
                .and(TEST_PLAN_MODULE.PARENT_ID.eq(module.getParentId()))
                .and(TEST_PLAN_MODULE.NAME.eq(module.getName()))
        ).exists();
        if (exists) {
            throw new CustomException(Translator.get("node.name.repeat"));
        }
    }

    protected Long countPos(String parentId) {
        Long maxPos = queryChain().select(QueryMethods.max(TestPlanModule::getPos))
                .where(TestPlanModule::getParentId).eq(parentId).objAs(Long.class);
        if (maxPos == null) {
            return LIMIT_POS;
        } else {
            return maxPos + LIMIT_POS;
        }
    }
}
