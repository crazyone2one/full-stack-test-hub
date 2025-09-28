package cn.master.hub.service.impl;

import cn.master.hub.constants.ApplicationNumScope;
import cn.master.hub.dto.api.ApiTestCaseAddRequest;
import cn.master.hub.entity.ApiTestCase;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.uid.NumGenerator;
import cn.master.hub.mapper.ApiTestCaseMapper;
import cn.master.hub.service.ApiTestCaseService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static cn.master.hub.dto.project.NodeSortUtils.DEFAULT_NODE_INTERVAL_POS;
import static cn.master.hub.entity.table.ApiTestCaseTableDef.API_TEST_CASE;

/**
 * 接口用例 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-26
 */
@Service
public class ApiTestCaseServiceImpl extends ServiceImpl<ApiTestCaseMapper, ApiTestCase> implements ApiTestCaseService {

    @Override
    public ApiTestCase addCase(ApiTestCaseAddRequest request, String userId) {
        ApiTestCase testCase = new ApiTestCase();
        BeanUtils.copyProperties(request, testCase);
        testCase.setNum(NumGenerator.nextNum(request.getProjectId(), ApplicationNumScope.API_TEST_CASE));
        testCase.setPos(getNextOrder(request.getProjectId()));
        checkProjectExist(testCase.getProjectId());
        checkNameExist(testCase);
        testCase.setCreateUser(userId);
        testCase.setCreateUser(userId);
        testCase.setRequest(getMsTestElementStr(request.getRequest()).getBytes());
        mapper.insert(testCase);
        return testCase;
    }

    private void checkProjectExist(String projectId) {
        if (!QueryChain.of(SystemProject.class).eq(SystemProject::getId, projectId).exists()) {
            throw new CustomException(Translator.get("project_is_not_exist"));
        }
    }

    private void checkNameExist(ApiTestCase testCase) {
        boolean exists = queryChain()
                .where(API_TEST_CASE.PROJECT_ID.eq(testCase.getProjectId())
                        .and(API_TEST_CASE.NAME.eq(testCase.getName()))
                        .and(API_TEST_CASE.ID.ne(testCase.getId())))
                .exists();
        if (exists) {
            throw new CustomException(Translator.get("api_test_case_name_exist"));
        }
    }

    private String getMsTestElementStr(@NotNull Object request) {
        return null;
    }

    private Long getNextOrder(String projectId) {
        Long pos = queryChain().select(API_TEST_CASE.POS).where(ApiTestCase::getProjectId).eq(projectId)
                .orderBy(API_TEST_CASE.POS.desc()).limit(1).oneAs(Long.class);
        return (pos == null ? 0 : pos) + DEFAULT_NODE_INTERVAL_POS;
    }
}
